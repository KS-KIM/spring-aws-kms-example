package io.kskim.awskmsexample.encryption.adapter.output

import com.amazonaws.encryptionsdk.AwsCrypto
import com.amazonaws.encryptionsdk.caching.CachingCryptoMaterialsManager
import io.kskim.awskmsexample.encryption.core.application.output.EncryptPort
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.util.*

@Component
class AwsKmsEncryptAdapter(
    private val crypto: AwsCrypto,
    @Qualifier("encryptCachingMaterialsManager")
    private val cachingMaterialsManager: CachingCryptoMaterialsManager,
) : EncryptPort {
    override fun encrypt(data: String): String {
        val cryptoResult = crypto.encryptData(
            cachingMaterialsManager,
            data.toByteArray(),
        )
        val encryptedData = cryptoResult.result
        return Base64.getEncoder().encodeToString(encryptedData)
    }
}
