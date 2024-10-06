package model

import kotlinx.serialization.Serializable
import org.eclipse.jetty.util.security.Password
import serializer.PasswordSerializer
import serializer.UUIDSerializer
import java.util.*

/**
 * Represents a User.
 *
 * @param uId the user’s identifier (unique).
 * @param username the username of the user.
 * @param password the password chosen by the user.
 * @param token the access token of each user.
 * @throws IllegalArgumentException if the username is empty.
 */
@Serializable
data class User(
    val uId: UInt? = null,
    val username: String,
	@Serializable(with = PasswordSerializer::class)
    val password: Password,
	@Serializable(with = UUIDSerializer::class)
    val token: UUID = UUID.randomUUID(),
) {
    init {
        require(username.isNotBlank()) { "Username must not be blank." }
    }
}
