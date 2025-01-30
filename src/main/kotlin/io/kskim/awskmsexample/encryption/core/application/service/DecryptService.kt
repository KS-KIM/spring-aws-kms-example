package io.kskim.awskmsexample.encryption.core.application.service

import io.kskim.awskmsexample.encryption.core.application.input.DecryptCommand
import io.kskim.awskmsexample.encryption.core.application.input.DecryptResponse
import io.kskim.awskmsexample.encryption.core.application.input.DecryptUseCase
import io.kskim.awskmsexample.encryption.core.application.output.DecryptPort
import org.springframework.stereotype.Service

@Service
class DecryptService(
    private val decryptPort: DecryptPort,
) : DecryptUseCase {
    override fun decrypt(command: DecryptCommand): DecryptResponse {
        val decryptedData = decryptPort.decrypt(command.data, command.context)
        return DecryptResponse(decryptedData)
    }
}
