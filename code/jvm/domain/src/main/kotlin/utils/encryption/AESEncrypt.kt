package utils.encryption

import java.security.Key
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object AESEncrypt : Encrypt {
    private const val ALGORITHM = "AES"
    private val KEY = System.getenv("AES_KEY") ?: throw IllegalStateException("AES_KEY not found")
    private val key: Key = SecretKeySpec(KEY.toByteArray(), ALGORITHM)
    private val cipher: Cipher = Cipher.getInstance(ALGORITHM)

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