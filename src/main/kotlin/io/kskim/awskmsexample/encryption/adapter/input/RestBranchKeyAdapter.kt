package io.kskim.awskmsexample.encryption.adapter.input

import io.kskim.awskmsexample.encryption.core.application.input.CreateBranchKeyCommand
import io.kskim.awskmsexample.encryption.core.application.input.CreateBranchKeyUseCase
import io.kskim.awskmsexample.encryption.core.application.input.RotateBranchKeyCommand
import io.kskim.awskmsexample.encryption.core.application.input.RotateBranchKeyUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 엔트포인트에 공개될 필요가 없지만 테스트를 위해 열어둠
 * 최초에 branch key를 생성하고 추후 rotate 할 때 사용할 수 있음
 */
@RequestMapping("/encryption/branch-keys")
@RestController
class RestBranchKeyAdapter(
    private val createBranchKeyUseCase: CreateBranchKeyUseCase,
    private val rotateBranchKeyUseCase: RotateBranchKeyUseCase,
) {
    @PostMapping
    fun createBranchKey(
        @RequestBody request: RestCreateBranchKeyRequest
    ): ResponseEntity<Void> {
        val command = CreateBranchKeyCommand(request.branchKeyId)
        createBranchKeyUseCase.createBranchKey(command)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/rotate")
    fun rotateBranchKey(
        @RequestBody request: RestRotateBranchKeyRequest
    ): ResponseEntity<Void> {
        val command = RotateBranchKeyCommand(request.branchKeyId)
        rotateBranchKeyUseCase.rotateBranchKey(command)
        return ResponseEntity.noContent().build()
    }
}
