package io.kskim.awskmsexample.encryption.core.application.input

interface EncryptUseCase {
    fun encrypt(data: EncryptCommand): EncryptResponse
}
