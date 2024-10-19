package jdbc

import model.channels.AccessControl
import model.channels.Channel
import model.channels.ChannelInfo
import model.channels.ChannelName
import model.channels.Visibility
import model.messages.Message
import model.users.Password
import model.users.User
import model.users.UserInfo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.sql.Connection
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MessageJDBCTest {
    companion object {
        private const val VALID_PASSWORD = "Password123"
        private val passwordDefault = Password(VALID_PASSWORD)

        private fun runWithConnection(block: (Connection) -> Unit) =
            TestSetup
                .dataSource
                .connection
                .let(block)

        private fun testSetup(block: MessageJDBC.(User, Channel, Message) -> Unit) =
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
                        ChannelJDBC(connection)
                            .createChannel(
                                Channel
                                    .createChannel(
                                        owner = UserInfo(uId, user.username),
                                        name = ChannelName("channel", user.username),
                                        accessControl = AccessControl.READ_WRITE,
                                        visibility = Visibility.PRIVATE,
                                    ),
                            ).let { channel ->
                                assertNotNull(channel)
                                val channelId = checkNotNull(channel.cId) { "Channel id is null" }
                                val jdbc = MessageJDBC(connection)
                                jdbc.createMessage(
                                    Message(
                                        msg = "Hello, World!",
                                        user = UserInfo(uId, user.username),
                                        channel = ChannelInfo(channelId, channel.name),
                                    ),
                                ).let { message ->
                                    assertNotNull(message)
                                    jdbc.block(user, channel, message)
                                }
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

    private fun createMessage(connection: Connection): Message? =
        UserJDBC(connection)
            .createUser(
                User(
                    username = "user${System.currentTimeMillis() + Random.nextInt()}",
                    password = passwordDefault,
                ),
            ).let { user ->
                assertNotNull(user)
                val uId = checkNotNull(user.uId) { "User id is null" }
                ChannelJDBC(connection)
                    .createChannel(
                        Channel
                            .createChannel(
                                owner = UserInfo(uId, user.username),
                                name = ChannelName("channel", user.username),
                                accessControl = AccessControl.READ_WRITE,
                                visibility = Visibility.PUBLIC,
                            ),
                    ).let { channel ->
                        assertNotNull(channel)
                        val channelId = checkNotNull(channel.cId) { "Channel id is null" }
                        MessageJDBC(connection)
                            .createMessage(
                                Message(
                                    msg = "Hello, World!",
                                    user = UserInfo(uId, user.username),
                                    channel = ChannelInfo(channelId, channel.name),
                                ),
                            )
                    }
            }

    @Test
    fun `create a new message and find it`() {
        runWithConnection { connection ->
            val message = createMessage(connection)
            assertNotNull(message, "Message is null")
        }
    }

    @Test
    fun `find a message by id`() {
        testSetup { _, _, message ->
            val id = checkNotNull(message.msgId) { "Message id is null" }
            val foundMessage = findById(id)
            assertNotNull(foundMessage, "Message is null")
        }
    }

    @Test
    fun `find messages by channel id`() {
        runWithConnection { connection ->
            repeat(5) {
                createMessage(connection)
            }
        }
        testSetup { _, channel, _ ->
            val id = checkNotNull(channel.cId) { "Channel id is null" }
            val messages = findMessagesByChannelId(id, 2u, 3u)
            assertNotNull(messages, "Messages are null")
        }
    }

    @Test
    fun `find all messages`() {
        runWithConnection { connection ->
            repeat(5) {
                createMessage(connection)
            }
        }
        testSetup { _, _, _ ->
            val messages = findAll(0, 11)
            val count = messages.size
            assertEquals(6, count)
            assertNotNull(messages, "Messages are null")
        }
    }

//    @Test
//    fun `update a message`() {
//        testSetup { _, _, message ->
//            val id = checkNotNull(message.msgId) { "Message id is null" }
//            val updatedMessage = message.copy(creationTime = Timestamp.valueOf(LocalDateTime.now()))
//            save(updatedMessage)
//            val foundMessage = findById(id)
//            assertEquals(updatedMessage, foundMessage)
//
//        }
//    }

    @Test
    fun `delete a message by id`() {
        testSetup { _, _, message ->
            val id = checkNotNull(message.msgId) { "Message id is null" }
            val deleted = deleteById(id)
            assertNotNull(deleted, "Message is not deleted")
        }
    }
}