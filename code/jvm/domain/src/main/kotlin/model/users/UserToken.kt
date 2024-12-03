package model.users

import java.sql.Timestamp
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Represents a user token.
 *
 * @property uId The ID of the user.
 * @property token The token.
 * @property creationDate The creation date of the token.
 * @property expirationDate The expiration date of the token.
 * @throws IllegalArgumentException If the creation date is after the expiration date.
 */
data class UserToken(
    val uId: UInt,
    val token: UUID = UUID.randomUUID(),
    val creationDate: Timestamp = Timestamp(System.currentTimeMillis()),
    val expirationDate: Timestamp = Timestamp.valueOf(creationDate.toLocalDateTime().plusWeeks(1)),
) {
    init {
        require(creationDate < expirationDate) { "The creation date must be before the expiration date" }
    }

    /**
     * Checks if the token is valid.
     */
    val isExpired: Boolean
        get() = expirationDate < Timestamp(System.currentTimeMillis())

    val expirationDateInInt: Int
        get() {
            val currTime = System.currentTimeMillis()
            val expirationTime = expirationDate.time
            val maxAgeInMillis = expirationTime - currTime
            return TimeUnit.MILLISECONDS.toSeconds(maxAgeInMillis).toInt()
        }
}