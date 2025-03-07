package io.kskim.awskmsexample.encryption.adapter.input

import io.kskim.awskmsexample.encryption.core.application.output.EncryptionContext

data class RestEncryptRequest(
    val data: String,
    val context: EncryptionContext,
)
