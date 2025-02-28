package io.kskim.awskmsexample.encryption.core.application.input

import io.kskim.awskmsexample.encryption.core.application.output.EncryptionContext

data class DecryptCommand(
    val data: String,
    val context: EncryptionContext,
)
