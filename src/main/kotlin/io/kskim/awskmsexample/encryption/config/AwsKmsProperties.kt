package io.kskim.awskmsexample.encryption.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "aws.kms.encryption")
data class AwsKmsProperties(
    val endpoint: String,
    val accountId: String,
    val accessKey: String,
    val secretKey: String,
    val keyId: String,
    val region: String,
    val maxEntryAge: Long,
    val maxEntryUses: Long,
    val maxCacheEntries: Int,
)
