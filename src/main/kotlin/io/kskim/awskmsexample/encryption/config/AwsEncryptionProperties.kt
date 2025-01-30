package io.kskim.awskmsexample.encryption.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "aws.encryption")
data class AwsEncryptionProperties(
    val kms: KmsProperties,
    val keystore: KeystoreProperties,
    val keyring: KeyringProperties,
) {
    data class KmsProperties(
        val region: String,
        val accessKeyId: String,
        val secretAccessKey: String,
        val endpoint: String,
        val keyArn: String,
    )

    data class KeystoreProperties(
        val region: String,
        val accessKeyId: String,
        val secretAccessKey: String,
        val endpoint: String,
        val tableName: String,
        val logicalName: String,
    )

    data class KeyringProperties(
        val cacheEntryCapacity: Int,
        val cacheTTLSeconds: Long,
        val branchKeyId: String,
    )
}