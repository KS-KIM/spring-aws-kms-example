package io.kskim.awskmsexample.encryption.config

import com.amazonaws.encryptionsdk.AwsCrypto
import com.amazonaws.encryptionsdk.CommitmentPolicy
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.kms.KmsClient
import software.amazon.cryptography.keystore.KeyStore
import software.amazon.cryptography.keystore.model.KMSConfiguration
import software.amazon.cryptography.keystore.model.KeyStoreConfig
import software.amazon.cryptography.materialproviders.IKeyring
import software.amazon.cryptography.materialproviders.MaterialProviders
import software.amazon.cryptography.materialproviders.model.CacheType
import software.amazon.cryptography.materialproviders.model.CreateAwsKmsHierarchicalKeyringInput
import software.amazon.cryptography.materialproviders.model.DefaultCache
import software.amazon.cryptography.materialproviders.model.MaterialProvidersConfig
import java.net.URI

@EnableConfigurationProperties(value = [AwsEncryptionProperties::class])
@Configuration
class AwsKmsConfig(
    private val encryptionProperties: AwsEncryptionProperties
) {
    @Bean
    fun crypto(): AwsCrypto {
        return AwsCrypto.builder()
            .withCommitmentPolicy(CommitmentPolicy.RequireEncryptRequireDecrypt)
            .build()
    }

    /**
     * AWS KMS client를 생성한다.
     * KMS 클라이언트를 통해 branch key를 생성하고, key store에 저장된 branch key를 복호화한다.
     */
    @Bean
    fun kmsClient(): KmsClient {
        return KmsClient.builder()
            .endpointOverride(URI.create(encryptionProperties.kms.endpoint))
            .region(Region.of(encryptionProperties.kms.region))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        encryptionProperties.kms.accessKeyId,
                        encryptionProperties.kms.secretAccessKey
                    )
                )
            )
            .build()
    }

    /**
     * keystore로 사용할 DynamoDB client를 생성한다.
     */
    @Bean
    fun dynamoDbClient(): DynamoDbClient {
        return DynamoDbClient.builder()
            .region(Region.of(encryptionProperties.keystore.region))
            .endpointOverride(URI.create(encryptionProperties.keystore.endpoint))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        encryptionProperties.keystore.accessKeyId,
                        encryptionProperties.keystore.secretAccessKey
                    )
                )
            )
            .build()
    }

    /**
     * branch key를 관리하기 위한 key store를 생성한다.
     * key store는 DynamoDB에 저장된다.
     */
    @Bean
    fun keyStore(kmsClient: KmsClient, dynamoDbClient: DynamoDbClient): KeyStore {
        return KeyStore.builder()
            .KeyStoreConfig(
                KeyStoreConfig.builder()
                    .ddbClient(dynamoDbClient)
                    .ddbTableName(encryptionProperties.keystore.tableName)
                    .logicalKeyStoreName(encryptionProperties.keystore.logicalName)
                    .kmsClient(kmsClient)
                    .kmsConfiguration(
                        KMSConfiguration.builder()
                            .kmsKeyArn(encryptionProperties.kms.keyArn)
                            .build()
                    )
                    .build()
            )
            .build()
    }

    /**
     * keyring을 생성한다.
     * keyring에서 data key 암복호화에 필요한 branch key를 관리한다.
     * single-tenant 환경인 경우 static branch key id를 사용할 수 있다.
     * multi-tenant 환경인 경우 BranchKeyIdSupplier를 구현하여 branch key를 관리하는 것이 권장된다.
     */
    @Bean
    fun keyring(keyStore: KeyStore): IKeyring {
        val materialProvider = MaterialProviders.builder()
            .MaterialProvidersConfig(MaterialProvidersConfig.builder().build())
            .build()
        val keyringInput = CreateAwsKmsHierarchicalKeyringInput.builder()
            .keyStore(keyStore)
            .branchKeyId(encryptionProperties.keyring.branchKeyId)
            .ttlSeconds(encryptionProperties.keyring.cacheTTLSeconds)
            .cache(
                CacheType.builder()
                    .Default(
                        DefaultCache.builder()
                            .entryCapacity(encryptionProperties.keyring.cacheEntryCapacity)
                            .build()
                    )
                    .build()
            )
            .build()
        return materialProvider.CreateAwsKmsHierarchicalKeyring(keyringInput)
    }
}
