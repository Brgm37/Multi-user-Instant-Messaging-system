package utils.encryption

import java.security.Key
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * AESEncrypt is an implementation of Encrypt using AES encryption.
 * @param key the key used for encryption
 */
class AESEncrypt(
    key: String,
) : Encrypt {
    private val algorithm = "AES"
    private val key: Key = SecretKeySpec(key.toByteArray(), algorithm)
    private val cipher: Cipher = Cipher.getInstance(algorithm)

    override fun encrypt(value: String): String {
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encryptedValue = cipher.doFinal(value.toByteArray())
        return Base64.getEncoder().encodeToString(encryptedValue)
    }

    override fun decrypt(value: String): String {
        cipher.init(Cipher.DECRYPT_MODE, key)
        val decodedValue = Base64.getDecoder().decode(value)
        val decryptedValue = cipher.doFinal(decodedValue)
        return String(decryptedValue)
    }
}