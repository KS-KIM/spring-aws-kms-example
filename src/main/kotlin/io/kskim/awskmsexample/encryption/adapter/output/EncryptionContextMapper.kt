package io.kskim.awskmsexample.encryption.adapter.output

import io.kskim.awskmsexample.encryption.core.application.output.EncryptionContext
import io.kskim.awskmsexample.encryption.core.application.output.EncryptionPurpose
import org.springframework.stereotype.Component

@Component
class EncryptionContextMapper {
    fun convert(context: EncryptionContext): Map<String, String> {
        return mapOf(
            "purpose" to context.purpose.name,
        )
    }

    fun convert(context: Map<String, String>): EncryptionContext {
        return EncryptionContext(
            purpose = EncryptionPurpose.valueOf(context["purpose"]!!),
        )
    }
}