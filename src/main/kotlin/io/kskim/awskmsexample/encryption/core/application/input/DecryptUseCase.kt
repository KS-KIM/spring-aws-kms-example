package io.kskim.awskmsexample.encryption.core.application.input

interface DecryptUseCase {
    fun decrypt(command: DecryptCommand): DecryptResponse
}
