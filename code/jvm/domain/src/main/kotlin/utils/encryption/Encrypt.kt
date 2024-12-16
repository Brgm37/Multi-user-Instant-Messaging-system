package utils.encryption

/**
 * Interface for encrypting and decrypting values
 */
interface Encrypt {
    /**
     * Encrypts a value
     */
    fun encrypt(value: String): String

    /**
     * Decrypts a value
     */
    fun decrypt(value: String): String
}