package model.users

import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.UUID

/**
 * Represents a user invitation to access the app.
 * @property inviterId The ID of the inviter.
 * @property expirationDate The expiration date of the invitation.
 * @property invitationCode The invitation code to join the app.
 * @property isExpired Checks if the invitation is expired.
 */
data class UserInvitation(
    val inviterId: UInt,
    val expirationDate: Timestamp,
    val invitationCode: UUID = UUID.randomUUID(),
) {
    /**
     * Checks if the invitation is expired.
     */
    val isExpired: Boolean get() = expirationDate < Timestamp.valueOf(LocalDateTime.now())
}