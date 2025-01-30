package io.kskim.awskmsexample.encryption.core.application.output

interface EncryptPort {
    fun encrypt(data: String, context: EncryptionContext): String
}
