package serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.eclipse.jetty.util.security.Password

/**
 * Serializer for the Password class.
 * This class is used to serialize and deserialize Password objects.
 * @see KSerializer
 * @see Password
 */
class PasswordSerializer : KSerializer<Password> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Password", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Password = Password(decoder.decodeString())

    override fun serialize(
        encoder: Encoder,
        value: Password,
    ) = encoder.encodeString(value.toString())
}
