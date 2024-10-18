package model.channels

import org.junit.jupiter.api.Test
import java.sql.Timestamp
import java.time.LocalDateTime

class ChannelInvitationTest {
    @Test
    fun `successful channel invitation instantiation test`() {
        ChannelInvitation(
            cId = 1u,
            expirationDate = Timestamp.valueOf(LocalDateTime.now().plusDays(1)),
            maxUses = 1u,
            accessControl = AccessControl.READ_WRITE,
        )
    }

    @Test
    fun `isExpired test`() {
        val expirationDate = Timestamp.valueOf(LocalDateTime.now().minusDays(1))
        val channelInvitation =
            ChannelInvitation(
                cId = 1u,
                expirationDate = expirationDate,
                maxUses = 1u,
                accessControl = AccessControl.READ_WRITE,
            )
        assert(channelInvitation.isExpired)
    }
}