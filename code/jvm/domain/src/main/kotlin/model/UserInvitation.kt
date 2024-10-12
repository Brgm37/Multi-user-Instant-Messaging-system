package model

import java.time.LocalDateTime
import java.util.UUID

/**
 * Represents a user invitation to access the app.
 * @param userId The ID of the inviter.
 * @param expirationDate The expiration date of the invitation.
 * @param invitationCode The invitation code to join the app.
 */
data class UserInvitation(
	val userId: UInt,
	val expirationDate: LocalDateTime,
	val invitationCode: UUID = UUID.randomUUID(),
) {
	/**
	 * Checks if the invitation is expired.
	 */
	val isExpired: Boolean get() = expirationDate.isBefore(LocalDateTime.now())
}
