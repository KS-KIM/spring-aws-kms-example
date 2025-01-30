package io.kskim.awskmsexample.encryption.core.application.service

import io.kskim.awskmsexample.encryption.core.application.input.RotateBranchKeyCommand
import io.kskim.awskmsexample.encryption.core.application.input.RotateBranchKeyUseCase
import io.kskim.awskmsexample.encryption.core.application.output.RotateBranchKeyPort
import org.springframework.stereotype.Service

@Service
class RotateBranchKeyService(
    private val createBranchKeyPort: RotateBranchKeyPort,
) : RotateBranchKeyUseCase {
    override fun rotateBranchKey(command: RotateBranchKeyCommand) {
        createBranchKeyPort.rotateBranchKey(command.branchKeyId)
    }
}
