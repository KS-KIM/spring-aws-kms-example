package io.kskim.awskmsexample.encryption.adapter.output

import com.amazonaws.encryptionsdk.AwsCrypto
import com.amazonaws.encryptionsdk.caching.CachingCryptoMaterialsManager
import io.kskim.awskmsexample.encryption.core.application.output.DecryptPort
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.util.*

@Component
class AwsKmsDecryptAdapter(
    private val crypto: AwsCrypto,
    @Qualifier("decryptCachingMaterialsManager")
    private val cachingMaterialsManager: CachingCryptoMaterialsManager,
) : DecryptPort {
    override fun decrypt(data: String): String {
        val encryptedData = Base64.getDecoder().decode(data)
        val cryptoResult = crypto.decryptData(
            cachingMaterialsManager,
            encryptedData,
        )
        val decryptedData = cryptoResult.result
        return String(decryptedData)
    }
}
