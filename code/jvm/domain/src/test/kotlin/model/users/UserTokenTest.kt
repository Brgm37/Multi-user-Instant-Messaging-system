package model.users

import org.junit.jupiter.api.assertThrows
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UserTokenTest {
    @Test
    fun `token is expired when expiration date is in the past`() {
        val pastDate = Timestamp.valueOf(LocalDateTime.now().minusDays(1))
        assertThrows<IllegalArgumentException> {
            UserToken(
                1u,
                UUID.randomUUID(),
                Timestamp(System.currentTimeMillis()),
                pastDate,
            )
        }
    }

    @Test
    fun `token is not expired when expiration date is in the future`() {
        val futureDate = Timestamp.valueOf(LocalDateTime.now().plusDays(1))
        val token = UserToken(1u, UUID.randomUUID(), Timestamp(System.currentTimeMillis()), futureDate)
        assertFalse(token.isExpired())
    }

    @Test
    fun `creation date must be before expiration date`() {
        val creationDate = Timestamp.valueOf(LocalDateTime.now().plusDays(1))
        val expirationDate = Timestamp.valueOf(LocalDateTime.now())
        assertFailsWith<IllegalArgumentException> {
            UserToken(1u, UUID.randomUUID(), creationDate, expirationDate)
        }
    }

    @Test
    fun `default token is generated if not provided`() {
        val futureDate = Timestamp.valueOf(LocalDateTime.now().plusDays(1))
        val token = UserToken(1u, expirationDate = futureDate)
        assertTrue(token.token.toString().isNotEmpty())
    }

    @Test
    fun `provided token is used`() {
        val futureDate = Timestamp.valueOf(LocalDateTime.now().plusDays(1))
        val uuid = UUID.randomUUID()
        val token = UserToken(1u, uuid, Timestamp(System.currentTimeMillis()), futureDate)
        assertEquals(token.token, uuid)
    }
}