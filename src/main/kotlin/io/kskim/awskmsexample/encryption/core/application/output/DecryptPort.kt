package io.kskim.awskmsexample.encryption.core.application.output

interface DecryptPort {
    fun decrypt(data: String, context: EncryptionContext): String
}
