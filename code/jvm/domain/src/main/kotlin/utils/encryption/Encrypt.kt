package utils.encryption

/**
 * Interface for encrypting and decrypting values
 */
interface Encrypt {
    fun encrypt(value: String): String

    fun decrypt(value: String): String
}