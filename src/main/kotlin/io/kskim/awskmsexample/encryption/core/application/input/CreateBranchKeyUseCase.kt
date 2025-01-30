package io.kskim.awskmsexample.encryption.core.application.input

interface CreateBranchKeyUseCase {
    fun createBranchKey(command: CreateBranchKeyCommand)
}