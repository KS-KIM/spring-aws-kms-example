package io.kskim.awskmsexample.encryption.adapter.input

import io.kskim.awskmsexample.encryption.core.application.input.DecryptCommand
import io.kskim.awskmsexample.encryption.core.application.input.DecryptUseCase
import io.kskim.awskmsexample.encryption.core.application.input.EncryptCommand
import io.kskim.awskmsexample.encryption.core.application.input.EncryptUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/encryption")
@RestController
class RestEncryptionAdapter(
    private val encryptUseCase: EncryptUseCase,
    private val decryptUseCase: DecryptUseCase,
) {
    @PostMapping("/encrypt")
    fun encrypt(
        @RequestBody request: RestEncryptRequest
    ): ResponseEntity<RestEncryptResponse> {
        val encryptedData = encryptUseCase.encrypt(EncryptCommand(request.data, request.context))
        return ResponseEntity.ok(RestEncryptResponse(encryptedData.encryptedData))
    }

    @PostMapping("/decrypt")
    fun decrypt(
        @RequestBody request: RestDecryptRequest
    ): ResponseEntity<RestDecryptResponse> {
        val decryptedData = decryptUseCase.decrypt(DecryptCommand(request.data, request.context))
        return ResponseEntity.ok(RestDecryptResponse(decryptedData.decryptedData))
    }
}
