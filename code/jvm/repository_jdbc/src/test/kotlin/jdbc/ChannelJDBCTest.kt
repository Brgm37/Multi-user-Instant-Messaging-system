package jdbc

import model.channels.AccessControl.READ_WRITE
import model.channels.Channel
import model.channels.ChannelInvitation
import model.channels.ChannelName
import model.channels.Visibility
import model.channels.Visibility.PRIVATE
import model.channels.Visibility.PUBLIC
import model.channels.decrementUses
import model.users.Password
import model.users.User
import model.users.UserInfo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource
import java.sql.Connection
import java.time.LocalDate
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ChannelJDBCTest {
    private val validPassword = "Password123"
    private val passwordDefault = Password(validPassword)

    companion object {
        private fun runWithConnection(block: (Connection) -> Unit) =
            PGSimpleDataSource()
                .apply { setURL("jdbc:postgresql://localhost:5433/daw_test?user=postgres&password=password") }
                .connection
                .let(block)
    }

    @BeforeEach
    fun clean() {
        runWithConnection { connection ->
            ChannelJDBC(connection).clear()
            UserJDBC(connection).clear()
            MessageJDBC(connection).clear()
        }
    }

    private fun createChannel(
        connection: Connection,
        visibility: Visibility = PUBLIC,
    ): Channel? =
        UserJDBC(connection)
            .createUser(
                User(
                    username = "user${System.currentTimeMillis() + Random.nextInt()}",
                    password = passwordDefault,
                ),
            ).let { user ->
                assertNotNull(user)
                assertNotNull(user.uId)
                val id = requireNotNull(user.uId) { "User id is null" }
                ChannelJDBC(connection)
                    .createChannel(
                        Channel
                            .createChannel(
                                owner = UserInfo(id, user.username),
                                name = ChannelName("channel", user.username),
                                accessControl = READ_WRITE,
                                visibility = visibility,
                            ),
                    )
            }

    @Test
    fun `create a channel and find it`() =
        runWithConnection { connection ->
            assertNotNull(createChannel(connection)?.channelId)
        }

    @Test
    fun `find a channel by id`() =
        runWithConnection { connection ->
            val channel = createChannel(connection)
            val id = requireNotNull(channel?.channelId) { "Channel id is null" }
            val foundChannel = ChannelJDBC(connection).findById(id)
            assertNotNull(foundChannel)
        }

    @Test
    fun `find a channel by name`() =
        runWithConnection { connection ->
            UserJDBC(connection)
                .createUser(
                    User(
                        username = "user",
                        password = passwordDefault,
                    ),
                ).let { user ->
                    assertNotNull(user)
                    val id = requireNotNull(user.uId) { "User id is null" }
                    val numberOfChannel = 5
                    repeat(numberOfChannel) {
                        Channel
                            .createChannel(
                                owner = UserInfo(id, user.username),
                                name = ChannelName("channel$it", user.username),
                                accessControl = READ_WRITE,
                                visibility = PUBLIC,
                            ).let { channel ->
                                ChannelJDBC(connection).createChannel(channel)
                            }
                    }
                    ChannelJDBC(connection)
                        .findByUserId(id, 0, 10)
                        .let { channels ->
                            assertTrue(channels.isNotEmpty())
                            assertEquals(numberOfChannel, channels.size)
                        }
                }
        }

    @Test
    fun `test update a channel`() {
        runWithConnection { connection ->
            val channel = createChannel(connection)
            assertIs<Channel.Public>(channel)
            val id = requireNotNull(channel.channelId) { "Channel id is null" }
            val updatedChannel =
                channel.copy(
                    name = ChannelName("new name", channel.owner.username),
                )
            ChannelJDBC(connection).save(updatedChannel)
            val foundChannel = ChannelJDBC(connection).findById(id)
            assertEquals(updatedChannel, foundChannel)
        }
    }

    @Test
    fun `test create a channel invitation`() {
        runWithConnection { connection ->
            val channel = createChannel(connection)
            assertNotNull(channel)
            val channelId = requireNotNull(channel.channelId) { "Channel id is null" }
            val invitation =
                ChannelInvitation(
                    channelId = channelId,
                    expirationDate = LocalDate.of(2027, 1, 1),
                    maxUses = 1u,
                    accessControl = READ_WRITE,
                )
            val channelRepo = ChannelJDBC(connection)
            channelRepo.createInvitation(invitation)
        }
    }

    @Test
    fun `test find a channel invitation`() {
        runWithConnection { connection ->
            val channel = createChannel(connection)
            assertNotNull(channel)
            val channelId = requireNotNull(channel.channelId) { "Channel id is null" }
            val invitation =
                ChannelInvitation(
                    channelId = channelId,
                    expirationDate = LocalDate.of(2027, 1, 1),
                    maxUses = 1u,
                    accessControl = READ_WRITE,
                )
            val channelRepo = ChannelJDBC(connection)
            channelRepo.createInvitation(invitation)
            val foundInvitation = channelRepo.findInvitation(channelId)
            assertEquals(invitation, foundInvitation)
        }
    }

    @Test
    fun `test update channel invitation`() {
        runWithConnection { connection ->
            val channel = createChannel(connection)
            assertNotNull(channel)
            val channelId = requireNotNull(channel.channelId) { "Channel id is null" }
            val invitation =
                ChannelInvitation(
                    channelId = channelId,
                    expirationDate = LocalDate.of(2027, 1, 1),
                    maxUses = 1u,
                    accessControl = READ_WRITE,
                )
            val channelRepo = ChannelJDBC(connection)
            channelRepo.createInvitation(invitation)
            val updatedInvitation = invitation.decrementUses()
            channelRepo.updateInvitation(updatedInvitation)
            val foundInvitation = channelRepo.findInvitation(channelId)
            assertEquals(updatedInvitation, foundInvitation)
        }
    }

    @Test
    fun `test delete channel invitation`() {
        runWithConnection { connection ->
            val channel = createChannel(connection)
            assertNotNull(channel)
            val channelId = requireNotNull(channel.channelId) { "Channel id is null" }
            val invitation =
                ChannelInvitation(
                    channelId = channelId,
                    expirationDate = LocalDate.of(2027, 1, 1),
                    maxUses = 1u,
                    accessControl = READ_WRITE,
                )
            val channelRepo = ChannelJDBC(connection)
            channelRepo.createInvitation(invitation)
            channelRepo.deleteInvitation(channelId)
            assertNull(channelRepo.findInvitation(channelId))
        }
    }

    @Test
    fun `test delete a channel`() {
        runWithConnection { connection ->
            val channel = createChannel(connection)
            val id = requireNotNull(channel?.channelId) { "Channel id is null" }
            ChannelJDBC(connection).deleteById(id)
            assertNull(ChannelJDBC(connection).findById(id))
        }
    }

    @Test
    fun `test find all channels`() {
        runWithConnection { connection ->
            val numberOfChannels = 5
            val channels =
                (0 until numberOfChannels).map {
                    createChannel(connection)
                }
            createChannel(connection, PRIVATE)
                .let { channel ->
                    assertIs<Channel.Private>(channel)
                    assertNotNull(channel.channelId)
                    val id = requireNotNull(channel.channelId) { "Channel id is null" }
                    assertNotNull(ChannelJDBC(connection).deleteById(id))
                }
            val foundChannels = ChannelJDBC(connection).findAll(0, 10)
            assertEquals(numberOfChannels, foundChannels.size)
            assertEquals(channels, foundChannels)
        }
    }
}