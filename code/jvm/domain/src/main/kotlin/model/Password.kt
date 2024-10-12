package model

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

	init {
		require(isValidPassword(value)) { "Invalid password." }
	}

	companion object {
		const val PASSWORD_MIN_LENGTH = 8
		const val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{$PASSWORD_MIN_LENGTH,}$"

		fun isValidPassword(password: String): Boolean = Regex(PASSWORD_PATTERN).matches(password)
	}
}
