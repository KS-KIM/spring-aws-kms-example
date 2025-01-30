package io.kskim.awskmsexample.encryption.core.application.output

interface CreateBranchKeyPort {
    fun createBranchKey(branchKeyId: String, context: EncryptionContext): String
}