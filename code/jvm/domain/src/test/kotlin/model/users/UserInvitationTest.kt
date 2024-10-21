package model.users

import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UserInvitationTest {
    @Test
    fun `invitation is expired when expiration date is in the past`() {
        val pastDate = Timestamp.valueOf(LocalDateTime.now().minusDays(1))
        val invitation = UserInvitation(1u, pastDate)
        assertTrue(invitation.isExpired)
    }

    @Test
    fun `invitation is not expired when expiration date is in the future`() {
        val futureDate = Timestamp.valueOf(LocalDateTime.now().plusDays(1))
        val invitation = UserInvitation(1u, futureDate)
        assertFalse(invitation.isExpired)
    }

    @Test
    fun `invitation code is generated if not provided`() {
        val futureDate = Timestamp.valueOf(LocalDateTime.now().plusDays(1))
        val invitation = UserInvitation(1u, futureDate)
        assertTrue(invitation.invitationCode.toString().isNotEmpty())
    }

    @Test
    fun `invitation code is used if provided`() {
        val futureDate = Timestamp.valueOf(LocalDateTime.now().plusDays(1))
        val uuid = UUID.randomUUID()
        val invitation = UserInvitation(1u, futureDate, uuid)
        assertEquals(invitation.invitationCode, uuid)
    }
}