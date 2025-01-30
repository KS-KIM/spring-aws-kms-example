package io.kskim.awskmsexample.encryption.adapter.output

import com.amazonaws.encryptionsdk.AwsCrypto
import io.kskim.awskmsexample.encryption.core.application.output.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.cryptography.keystore.KeyStore
import software.amazon.cryptography.keystore.model.CreateKeyInput
import software.amazon.cryptography.keystore.model.VersionKeyInput
import software.amazon.cryptography.materialproviders.IKeyring
import java.util.*

@Component
class AwsKmsAdapter(
    private val crypto: AwsCrypto,
    private val keystore: KeyStore,
    private val keyring: IKeyring,
    private val encryptionContextMapper: EncryptionContextMapper,
) : CreateBranchKeyPort,
    RotateBranchKeyPort,
    EncryptPort,
    DecryptPort {
    @Value("\${spring.profiles.active:default}")
    private lateinit var environment: String

    override fun createBranchKey(branchKeyId: String, context: EncryptionContext): String {
        val result = keystore.CreateKey(
            CreateKeyInput.builder()
                .branchKeyIdentifier(branchKeyId)
                .encryptionContext(convertEncryptionContext(context))
                .build()
        )
        return result.branchKeyIdentifier()
    }

    override fun rotateBranchKey(branchKeyId: String) {
        keystore.VersionKey(
            VersionKeyInput.builder()
                .branchKeyIdentifier(branchKeyId)
                .build()
        )
        return
    }

    override fun decrypt(data: String, context: EncryptionContext): String {
        val ciphertext = Base64.getDecoder().decode(data)
        val cryptoResult = crypto.decryptData(
            keyring,
            ciphertext,
            convertEncryptionContext(context),
        )
        val decryptedData = cryptoResult.result
        return String(decryptedData)
    }

    override fun encrypt(data: String, context: EncryptionContext): String {
        val plaintext = data.toByteArray()
        val cryptoResult = crypto.encryptData(
            keyring,
            plaintext,
            convertEncryptionContext(context),
        )
        val encryptedData = cryptoResult.result
        return Base64.getEncoder().encodeToString(encryptedData)
    }

    private fun convertEncryptionContext(context: EncryptionContext): Map<String, String> {
        return encryptionContextMapper.convert(context).plus(
            "environment" to environment
        )
    }
}
