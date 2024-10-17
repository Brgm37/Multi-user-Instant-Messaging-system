package utils.encryption

/**
 * DummyEncrypt is a dummy implementation of Encrypt
 */
object DummyEncrypt : Encrypt {
    override fun encrypt(value: String): String = value

    override fun decrypt(value: String): String = value
}