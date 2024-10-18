package utils.encryption

import org.junit.jupiter.api.Test

class AESEncryptTest {
    @Test
    fun `successful encryption test`() {
        val key = "1234567890123456"
        val value = "value"
        val encrypt = AESEncrypt(key)
        val encryptedValue = encrypt.encrypt(value)
        assert(value != encryptedValue)
        val decryptedValue = encrypt.decrypt(encryptedValue)
        assert(value == decryptedValue)
    }
}