package io.kskim.awskmsexample.encryption.core.application.output

interface RotateBranchKeyPort {
    fun rotateBranchKey(branchKeyId: String)
}