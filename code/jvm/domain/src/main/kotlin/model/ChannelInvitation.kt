package model

import java.time.LocalDateTime
import java.util.UUID

/**
 * Represents a channel invitation.
 * @param channelId The ID of the channel associated to this invitation.
 * @param expirationDate The expiration date of the invitation.
 * @param maxUses The maximum number of uses of the invitation.
 */
data class ChannelInvitation(
	val channelId: UInt,
	val expirationDate: LocalDateTime,
	val maxUses: UInt,
	val invitationCode: UUID = UUID.randomUUID(),
) {
	/**
	 * Checks if the invitation is expired.
	 * @return True if the invitation is expired, false otherwise.
	 */
	val isExpired: Boolean get() = expirationDate.isBefore(LocalDateTime.now())
}

/**
 * Decrements the number of uses of the invitation.
 * @return The updated invitation.
 */
fun ChannelInvitation.decrementUses(): ChannelInvitation = copy(maxUses = maxUses - 1u)
