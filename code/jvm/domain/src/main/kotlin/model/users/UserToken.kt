package model.users

import java.sql.Timestamp
import java.util.UUID

/**
 * Represents a user token.
 *
 * @property userId The ID of the user.
 * @property token The token.
 * @property creationDate The creation date of the token.
 * @property expirationDate The expiration date of the token.
 * @throws IllegalArgumentException If the creation date is after the expiration date.
 */
data class UserToken(
    val userId: UInt,
    val token: UUID = UUID.randomUUID(),
    val creationDate: Timestamp = Timestamp(System.currentTimeMillis()),
    val expirationDate: Timestamp = Timestamp.valueOf(creationDate.toLocalDateTime().plusWeeks(1)),
) {
    init {
        require(creationDate < expirationDate) { "The creation date must be before the expiration date" }
    }

    /**
     * Checks if the token is valid.
     *
     * @return true if the token is valid, false otherwise.
     */
    fun isExpired(): Boolean = expirationDate < Timestamp(System.currentTimeMillis())
}