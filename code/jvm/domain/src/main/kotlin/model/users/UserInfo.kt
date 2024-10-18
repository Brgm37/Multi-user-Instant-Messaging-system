package model.users

/**
 * Represents a User.
 *
 * @property uId the user’s identifier (unique).
 * @property username the username of the user.
 * @throws IllegalArgumentException if the username is empty.
 */
data class UserInfo(
    val uId: UInt,
    val username: String,
) {
    init {
        require(username.isNotBlank()) { "Username must not be blank." }
    }
}