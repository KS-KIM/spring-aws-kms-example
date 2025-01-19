package io.kskim.awskmsexample.encryption.config

import com.amazonaws.encryptionsdk.AwsCrypto
import com.amazonaws.encryptionsdk.CommitmentPolicy
import com.amazonaws.encryptionsdk.MasterKeyProvider
import com.amazonaws.encryptionsdk.caching.CachingCryptoMaterialsManager
import com.amazonaws.encryptionsdk.caching.LocalCryptoMaterialsCache
import com.amazonaws.encryptionsdk.kms.DiscoveryFilter
import com.amazonaws.encryptionsdk.kmssdkv2.KmsMasterKey
import com.amazonaws.encryptionsdk.kmssdkv2.KmsMasterKeyProvider
import com.amazonaws.encryptionsdk.multi.MultipleProviderFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.kms.KmsClient
import java.net.URI
import java.util.concurrent.TimeUnit

@EnableConfigurationProperties(value = [AwsKmsProperties::class])
@Configuration
class AwsKmsConfig(
    private val awsKmsProperties: AwsKmsProperties
) {
    @Bean
    fun crypto(): AwsCrypto {
        return AwsCrypto.builder()
            .withCommitmentPolicy(CommitmentPolicy.RequireEncryptRequireDecrypt)
            .build()
    }

    @Bean
    fun kmsClient(): KmsClient {
        return KmsClient.builder()
            .endpointOverride(URI.create(awsKmsProperties.endpoint))
            .region(Region.of(awsKmsProperties.region))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(awsKmsProperties.accessKey, awsKmsProperties.secretKey)
                )
            )
            .build()
    }

    @Bean("encryptCachingMaterialsManager")
    fun encryptCachingMaterialsManager(kmsClient: KmsClient): CachingCryptoMaterialsManager {
        return CachingCryptoMaterialsManager.newBuilder()
            .withMasterKeyProvider(encryptMasterKeyProvider(kmsClient))
            .withCache(LocalCryptoMaterialsCache(awsKmsProperties.maxCacheEntries))
            .withMaxAge(awsKmsProperties.maxEntryAge, TimeUnit.MILLISECONDS)
            .withMessageUseLimit(awsKmsProperties.maxEntryUses)
            .build()
    }

    @Bean("decryptCachingMaterialsManager")
    fun decryptCachingMaterialsManager(kmsClient: KmsClient): CachingCryptoMaterialsManager {
        return CachingCryptoMaterialsManager.newBuilder()
            .withMasterKeyProvider(decryptMasterKeyProvider(kmsClient))
            .withCache(LocalCryptoMaterialsCache(awsKmsProperties.maxCacheEntries))
            .withMaxAge(awsKmsProperties.maxEntryAge, TimeUnit.MILLISECONDS)
            .build()
    }

    @Bean("encryptMasterKeyProvider")
    fun encryptMasterKeyProvider(kmsClient: KmsClient): MasterKeyProvider<KmsMasterKey> {
        val masterKey = KmsMasterKeyProvider.builder()
            .defaultRegion(Region.of(awsKmsProperties.region))
            .customRegionalClientSupplier { region -> kmsClient }
            .buildStrict(awsKmsProperties.keyId)

        return MultipleProviderFactory.buildMultiProvider(KmsMasterKey::class.java, masterKey)
    }

    @Bean("descriptMasterKeyProvider")
    fun decryptMasterKeyProvider(kmsClient: KmsClient): MasterKeyProvider<KmsMasterKey> {
        val masterKey = KmsMasterKeyProvider.builder()
            .defaultRegion(Region.of(awsKmsProperties.region))
            .customRegionalClientSupplier { region -> kmsClient }
            .buildDiscovery(DiscoveryFilter("aws", listOf(awsKmsProperties.accountId)))

        return MultipleProviderFactory.buildMultiProvider(KmsMasterKey::class.java, masterKey)
    }

    @Bean
    fun credentialProvider(): AwsCredentialsProvider {
        return DefaultCredentialsProvider.builder().build()
    }
}