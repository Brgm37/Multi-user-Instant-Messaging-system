package model.channels

import java.sql.Timestamp
import java.time.LocalDate
import java.util.UUID

/**
 * Represents a channel invitation.
 * @property cId The ID of the channel associated to this invitation.
 * @property expirationDate The expiration date of the invitation.
 * @property accessControl The accessControl associated to this invitation.
 * @property maxUses The maximum number of uses of the invitation.
 * @property invitationCode The invitation code associated to this invitation.
 */
data class ChannelInvitation(
    val cId: UInt,
    val expirationDate: Timestamp,
    val maxUses: UInt,
    val accessControl: AccessControl,
    val invitationCode: UUID = UUID.randomUUID(),
) {
    /**
     * Checks if the invitation is expired.
     */
    val isExpired: Boolean get() = expirationDate.toLocalDateTime().toLocalDate() < LocalDate.now()
}

/**
 * Decrements the number of uses of the invitation.
 * @return The updated invitation.
 */
fun ChannelInvitation.decrementUses(): ChannelInvitation = copy(maxUses = maxUses - 1u)