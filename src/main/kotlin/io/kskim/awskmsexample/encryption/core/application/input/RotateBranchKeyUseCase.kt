package io.kskim.awskmsexample.encryption.core.application.input

interface RotateBranchKeyUseCase {
    fun rotateBranchKey(command: RotateBranchKeyCommand)
}