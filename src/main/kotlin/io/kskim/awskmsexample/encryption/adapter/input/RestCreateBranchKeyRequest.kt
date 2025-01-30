package io.kskim.awskmsexample.encryption.adapter.input

import io.kskim.awskmsexample.encryption.core.application.output.EncryptionContext

data class RestCreateBranchKeyRequest(
    val branchKeyId: String,
    val context: EncryptionContext,
)