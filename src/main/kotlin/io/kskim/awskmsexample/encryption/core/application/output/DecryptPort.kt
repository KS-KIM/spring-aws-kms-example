package io.kskim.awskmsexample.encryption.core.application.output

interface DecryptPort {
    fun decrypt(data: String): String
}
