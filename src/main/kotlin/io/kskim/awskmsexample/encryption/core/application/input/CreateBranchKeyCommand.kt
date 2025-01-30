package io.kskim.awskmsexample.encryption.core.application.input

import io.kskim.awskmsexample.encryption.core.application.output.EncryptionContext

data class CreateBranchKeyCommand(
    val branchKeyId: String,
    val context: EncryptionContext,
)