package model.users

import java.sql.Timestamp
import java.util.UUID

data class UserToken(
    val token: UUID = UUID.randomUUID(),
    val userId: UInt,
    val creationDate: Timestamp = Timestamp(System.currentTimeMillis()),
    val expirationDate: Timestamp,
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