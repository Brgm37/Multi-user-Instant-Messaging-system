package model.users

/**
 * Represents a password.
 * Passwords must have:
 * - At least 8 characters.
 * - At least one digit.
 * - At least one uppercase letter.
 * - At least one lowercase letter.
 *
 * @param value the password value.
 * @throws IllegalArgumentException if the password is invalid.
 */
data class Password(
    val value: String,
) {
    override fun toString(): String = value

    /**
     * Checks if the password matches the given password.
     *
     * @param password the password to check.
     * @return true if the password matches the given password, false otherwise.
     */
    fun matches(password: String): Boolean = value == password

    init {
        require(isValidPassword(value)) { "Invalid password." }
    }

    companion object {
        private const val PASSWORD_MIN_LENGTH = 8
        const val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{$PASSWORD_MIN_LENGTH,}$"

        /**
         * Checks if the given password follows the PASSWORD_PATTERN.
         *
         * @param password the password to check.
         * @return true if the password is valid, false otherwise.
         */
        fun isValidPassword(password: String): Boolean = Regex(PASSWORD_PATTERN).matches(password)
    }
}