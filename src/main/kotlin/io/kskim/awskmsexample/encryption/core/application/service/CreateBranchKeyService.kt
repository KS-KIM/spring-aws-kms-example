package io.kskim.awskmsexample.encryption.core.application.service

import io.kskim.awskmsexample.encryption.core.application.input.CreateBranchKeyCommand
import io.kskim.awskmsexample.encryption.core.application.input.CreateBranchKeyUseCase
import io.kskim.awskmsexample.encryption.core.application.output.CreateBranchKeyPort
import org.springframework.stereotype.Service

@Service
class CreateBranchKeyService(
    private val createBranchKeyPort: CreateBranchKeyPort,
) : CreateBranchKeyUseCase {
    override fun createBranchKey(command: CreateBranchKeyCommand) {
        createBranchKeyPort.createBranchKey(command.branchKeyId)
    }
}
