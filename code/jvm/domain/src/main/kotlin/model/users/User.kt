package model.users

/**
 * Represents a User.
 *
 * @property uId the user’s identifier (unique).
 * @property username the username of the user.
 * @property password the password chosen by the user.
 * @throws IllegalArgumentException if the username is empty.
 */
data class User(
    val uId: UInt? = null,
    val username: String,
    val password: Password,
) {
    init {
        require(username.isNotBlank()) { "Username must not be blank." }
    }
}