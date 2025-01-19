package io.kskim.awskmsexample.encryption.core.application.service

import io.kskim.awskmsexample.encryption.core.application.input.EncryptCommand
import io.kskim.awskmsexample.encryption.core.application.input.EncryptResponse
import io.kskim.awskmsexample.encryption.core.application.input.EncryptUseCase
import io.kskim.awskmsexample.encryption.core.application.output.EncryptPort
import org.springframework.stereotype.Service

@Service
class EncryptService(
    private val encryptPort: EncryptPort,
) : EncryptUseCase {
    override fun encrypt(command: EncryptCommand): EncryptResponse {
        val encryptedData = encryptPort.encrypt(command.data)
        return EncryptResponse(encryptedData)
    }
}
