package model.users

/**
 * Represents a User.
 *
 * @param uId the user’s identifier (unique).
 * @param username the username of the user.
 * @param password the password chosen by the user.
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