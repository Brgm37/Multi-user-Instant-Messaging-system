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
import java.sql.Connection
import java.sql.Timestamp
import java.time.LocalDateTime
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ChannelJDBCTest {
    companion object {
        private const val VALID_PASSWORD = "Password123"
        private val passwordDefault = Password(VALID_PASSWORD)

        private fun runWithConnection(block: (Connection) -> Unit) =
            TestSetup
                .dataSource
                .connection
                .let(block)

        private val expirationDate: Timestamp
            get() = Timestamp.valueOf(LocalDateTime.now().plusWeeks(1))

        private fun testSetup(block: ChannelJDBC.(User, Channel) -> Unit) =
            runWithConnection { connection ->
                UserJDBC(connection)
                    .createUser(
                        User(
                            username = "user",
                            password = passwordDefault,
                        ),
                    ).let { user ->
                        assertNotNull(user)
                        val uId = checkNotNull(user.uId) { "User id is null" }
                        val jdbc = ChannelJDBC(connection)
                        jdbc
                            .createChannel(
                                Channel
                                    .createChannel(
                                        owner = UserInfo(uId, user.username),
                                        name = ChannelName("channel", user.username),
                                        accessControl = READ_WRITE,
                                        visibility = PRIVATE,
                                    ),
                            ).let { channel ->
                                assertNotNull(channel)
                                jdbc.block(user, channel)
                            }
                    }
            }
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
            val channel = createChannel(connection)
            assertNotNull(channel, "Channel is null")
        }

    @Test
    fun `find a channel by id`() =
        testSetup { _, channel ->
            val id = checkNotNull(channel.cId) { "Channel id is null" }
            val foundChannel = findById(id)
            assertEquals(channel, foundChannel)
        }

    @Test
    fun `find public channels`() =
        testSetup { user, _ ->
            val uId = checkNotNull(user.uId) { "User id is null" }
            val n = 10
            repeat(n) {
                createChannel(
                    Channel.createChannel(
                        owner = UserInfo(uId, user.username),
                        name = ChannelName("channel$it", user.username),
                        accessControl = READ_WRITE,
                        visibility = PUBLIC,
                    ),
                )
            }
            val channels = findAll(0u, n.toUInt())
            assertEquals(n, channels.size)
        }

    @Test
    fun `test update a channel`() =
        testSetup { user, channel ->
            val id = checkNotNull(channel.cId) { "Channel id is null" }
            assertIs<Channel.Private>(channel, "Channel is not private")
            val updatedChannel = channel.copy(name = ChannelName("newName", user.username))
            save(updatedChannel)
            val foundChannel = findById(id)
            assertEquals(updatedChannel, foundChannel)
        }

    @Test
    fun `test create a channel invitation`() =
        testSetup { _, channel ->
            val id = checkNotNull(channel.cId) { "Channel id is null" }
            val invitation =
                ChannelInvitation(
                    cId = id,
                    expirationDate = expirationDate,
                    maxUses = 1u,
                    accessControl = READ_WRITE,
                )
            createInvitation(invitation)
        }

    @Test
    fun `test find a channel invitation`() =
        testSetup { _, channel ->
            val id = checkNotNull(channel.cId) { "Channel id is null" }
            val invitation =
                ChannelInvitation(
                    cId = id,
                    expirationDate = Timestamp(expirationDate.time),
                    maxUses = 1u,
                    accessControl = READ_WRITE,
                )
            createInvitation(invitation)
            val foundInvitation = findInvitation(id)
            assertEquals(invitation, foundInvitation)
        }

    @Test
    fun `test update channel invitation`() =
        testSetup { _, channel ->
            val id = checkNotNull(channel.cId) { "Channel id is null" }
            val invitation =
                ChannelInvitation(
                    cId = id,
                    expirationDate = Timestamp(expirationDate.time),
                    maxUses = 1u,
                    accessControl = READ_WRITE,
                )
            createInvitation(invitation)
            val updatedInvitation = invitation.decrementUses()
            updateInvitation(updatedInvitation)
            val foundInvitation = findInvitation(id)
            assertEquals(updatedInvitation, foundInvitation)
        }

    @Test
    fun `test delete channel invitation`() =
        testSetup { _, channel ->
            val id = checkNotNull(channel.cId) { "Channel id is null" }
            val invitation =
                ChannelInvitation(
                    cId = id,
                    expirationDate = Timestamp(expirationDate.time),
                    maxUses = 1u,
                    accessControl = READ_WRITE,
                )
            createInvitation(invitation)
            assertNotNull(findInvitation(id))
            deleteInvitation(id)
            assertNull(findInvitation(id))
        }

    @Test
    fun `test delete a channel`() =
        testSetup { _, channel ->
            val id = checkNotNull(channel.cId) { "Channel id is null" }
            assertNotNull(findById(id))
            assertNotNull(deleteById(id))
            assertNull(findById(id))
        }

    @Test
    fun `test join Channel`() =
        testSetup { _, channel ->
            val newUser =
                UserJDBC(
                    TestSetup
                        .dataSource
                        .connection,
                ).createUser(
                    User(
                        username = "newUser",
                        password = passwordDefault,
                    ),
                )
            assertNotNull(newUser, "User is null")
            val uId = checkNotNull(newUser.uId) { "User id is null" }
            val cId = checkNotNull(channel.cId) { "Channel id is null" }
            joinChannel(cId, uId, READ_WRITE)
            assertTrue(isUserInChannel(cId, uId))
        }

    @Test
    fun `test is user in channel`() =
        testSetup { user, channel ->
            val uId = checkNotNull(user.uId) { "User id is null" }
            val cId = checkNotNull(channel.cId) { "Channel id is null" }
            assertTrue(isUserInChannel(cId, uId))
        }

    @Test
    fun `find user access control in channel`() =
        testSetup { user, channel ->
            val uId = checkNotNull(user.uId) { "User id is null" }
            val cId = checkNotNull(channel.cId) { "Channel id is null" }
            assertEquals(READ_WRITE, findUserAccessControl(cId, uId))
        }

    @Test
    fun `find by userId`() =
        testSetup { user, _ ->
            val uId = checkNotNull(user.uId) { "User id is null" }
            val foundChannels = findByUserId(uId, 0, 10)
            val expectedSize = 1
            assertEquals(expectedSize, foundChannels.size)
        }

    @Test
    fun `find by name`() =
        testSetup { _, channel ->
            val name = checkNotNull(channel.name) { "Channel name is null" }
            val foundChannels = findByName(name.fullName)
            assertNotNull(foundChannels) { "Channel is null" }
        }

    @Test
    fun `find by partial name`() =
        testSetup { user, _ ->
            val uId = checkNotNull(user.uId) { "User id is null" }
            val newChannel =
                createChannel(
                    Channel.createChannel(
                        owner = UserInfo(uId, user.username),
                        name = ChannelName("new_channel", user.username),
                        accessControl = READ_WRITE,
                        visibility = PUBLIC,
                    ),
                )
            val name = checkNotNull(newChannel?.name) { "Channel name is null" }
            val foundChannels = findByName(name.name, 0u, 10u)
            assertEquals(newChannel, foundChannels.first())
        }
}