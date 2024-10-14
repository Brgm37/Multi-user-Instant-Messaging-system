package model.users

import java.util.UUID

/**
 * Represents a User.
 *
 * @param uId the user’s identifier (unique).
 * @param username the username of the user.
 * @param password the password chosen by the user.
 * @param token the access token of each user.
 * @throws IllegalArgumentException if the username is empty.
 */
data class User(
    val uId: UInt? = null,
    val username: String,
    val password: Password,
    val token: UUID = UUID.randomUUID(),
) {
    init {
        require(username.isNotBlank()) { "Username must not be blank." }
    }
}