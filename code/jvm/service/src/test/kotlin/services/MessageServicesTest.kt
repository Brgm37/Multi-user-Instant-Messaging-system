package services

import TransactionManager
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import errors.MessageError
import jdbc.transactionManager.TransactionManagerJDBC
import mem.TransactionManagerInMem
import model.channels.AccessControl
import model.channels.AccessControl.READ_ONLY
import model.channels.AccessControl.READ_WRITE
import model.channels.Channel
import model.channels.ChannelName
import model.channels.Visibility
import model.channels.Visibility.PRIVATE
import model.channels.Visibility.PUBLIC
import model.messages.Message
import model.users.Password
import model.users.User
import model.users.UserInfo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import utils.Failure
import utils.Success
import utils.encryption.DummyEncrypt
import java.sql.Timestamp
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class MessageServicesTest {
    companion object {
        @JvmStatic
        fun transactionManagers(): Stream<TransactionManager> =
            Stream.of(
                TransactionManagerInMem().also { cleanup(it) },
                TransactionManagerJDBC(TestSetup.dataSource, DummyEncrypt).also { cleanup(it) },
            )

        private fun cleanup(manager: TransactionManager) =
            manager.run {
                channelRepo.clear()
                userRepo.clear()
                messageRepo.clear()
            }

        private fun makeUser(
            manager: TransactionManager,
            username: String,
        ) = manager
            .run {
                userRepo
                    .createUser(
                        User(
                            username = username,
                            password = Password("Password123"),
                        ),
                    )
            }

        private fun userJoinChannel(
            manager: TransactionManager,
            user: User,
            channel: Channel,
            accessControl: AccessControl,
        ) = manager
            .run {
                val userId = checkNotNull(user.uId) { "User id is null" }
                val channelId = checkNotNull(channel.channelId) { "Channel id is null" }
                channelRepo.joinChannel(channelId, userId, accessControl)
            }

        private fun makeChannel(
            manager: TransactionManager,
            user: User,
            accessControl: AccessControl,
            visibility: Visibility,
        ) = manager
            .run {
                val ownerId = checkNotNull(user.uId) { "Owner id is null" }
                channelRepo
                    .createChannel(
                        Channel.createChannel(
                            owner = UserInfo(ownerId, user.username),
                            name = ChannelName("Channel", user.username),
                            accessControl = accessControl,
                            visibility = visibility,
                        ),
                    )
            }
    }

    private fun makeTestEnv(
        visibility: Visibility,
        accessControl: AccessControl,
        manager: TransactionManager,
        block: (TransactionManager, User, User, Channel, MessageServices) -> Unit,
    ) {
        val messageServices = MessageServices(manager)
        val owner = makeUser(manager, username)
        val user = checkNotNull(makeUser(manager, username))
        val channel = makeChannel(manager, checkNotNull(owner), accessControl, visibility)
        checkNotNull(channel) { "Channel is null" }
        block(manager, owner, user, channel, messageServices)
    }

    private fun makeUserName(): () -> String {
        var count = 0
        return {
            count += 1
            "User$count"
        }
    }

    private val userNameMaker = makeUserName()
    private val username: String
        get() {
            return userNameMaker()
        }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `create a new message`(m: TransactionManager) {
        makeTestEnv(PUBLIC, READ_WRITE, m) { manager, _, user, channel, messageServices ->
            userJoinChannel(manager, user, channel, READ_WRITE)
            val newMessage =
                messageServices
                    .createMessage(
                        "Hello, World!",
                        checkNotNull(user.uId) { "User id is null" },
                        checkNotNull(channel.channelId) { "Channel id is null" },
                    )
            assertIs<Success<Message>>(newMessage, "Message creation failed with error")
            assertNotNull(newMessage.value.msgId, "Message id is null")
            assertEquals(user.uId, newMessage.value.user.uId, "User id is different")
            assertEquals(
                channel.channelId,
                newMessage.value.channel.channelId,
                "Channel id is different",
            )
            assertEquals("Hello, World!", newMessage.value.msg, "Message is different")
        }
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `fail to create a new message due to blank message`(m: TransactionManager) {
        makeTestEnv(PUBLIC, READ_WRITE, m) { manager, _, user, channel, messageServices ->
            userJoinChannel(manager, user, channel, READ_WRITE)
            val newMessage =
                messageServices
                    .createMessage(
                        "",
                        checkNotNull(user.uId) { "User id is null" },
                        checkNotNull(channel.channelId) { "Channel id is null" },
                    )
            assertIs<Failure<MessageError.InvalidMessageInfo>>(
                newMessage,
                "Message creation should have failed",
            )
            assertEquals(MessageError.InvalidMessageInfo, newMessage.value, "Message error is different")
        }
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `fail to create message due to user not belonging to the channel`(m: TransactionManager) {
        makeTestEnv(PUBLIC, READ_WRITE, m) { _, _, user, channel, messageServices ->
            val newMessage =
                messageServices
                    .createMessage(
                        "Hello, World!",
                        checkNotNull(user.uId) { "User id is null" },
                        checkNotNull(channel.channelId) { "Channel id is null" },
                    )
            assertIs<Failure<MessageError.UserNotInChannel>>(newMessage, "Message creation should have failed")
            assertEquals(MessageError.UserNotInChannel, newMessage.value, "Message error is different")
        }
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `Public channel READ_ONLY message creation success and fail due to access control`(m: TransactionManager) {
        makeTestEnv(PUBLIC, READ_ONLY, m) { manager, owner, user, channel, messageServices ->
            userJoinChannel(manager, user, channel, READ_WRITE)
            val newMessageFailure =
                messageServices
                    .createMessage(
                        "Hello, World!",
                        checkNotNull(user.uId) { "User id is null" },
                        checkNotNull(channel.channelId) { "Channel id is null" },
                    )
            val newMessageSuccess =
                messageServices
                    .createMessage(
                        "Hello, World!",
                        checkNotNull(owner.uId) { "Owner id is null" },
                        checkNotNull(channel.channelId) { "Channel id is null" },
                    )
            assertIs<Success<Message>>(newMessageSuccess, "Message creation failed with error")
            assertNotNull(newMessageSuccess.value.msgId, "Message id is null")
            assertEquals(owner.uId, newMessageSuccess.value.user.uId, "User id is different")
            assertEquals(
                channel.channelId,
                newMessageSuccess.value.channel.channelId,
                "Channel id is different",
            )
            assertEquals("Hello, World!", newMessageSuccess.value.msg, "Message is different")
            assertIs<Failure<MessageError.UserDoesNotHaveAccess>>(
                newMessageFailure,
                "Message creation should have failed",
            )
            assertEquals(
                MessageError.UserDoesNotHaveAccess,
                newMessageFailure.value,
                "Message error is different",
            )
        }
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `Private channel message creation success and fail due to access control`(manager: TransactionManager) {
        makeTestEnv(PRIVATE, READ_WRITE, manager) { m, owner, user, channel, messageServices ->
            userJoinChannel(m, user, channel, READ_ONLY)
            val newMessageSuccess =
                messageServices
                    .createMessage(
                        "Hello, World!",
                        checkNotNull(owner.uId) { "User id is null" },
                        checkNotNull(channel.channelId) { "Channel id is null" },
                    )
            val newMessageFailure =
                messageServices
                    .createMessage(
                        "Hello, World!",
                        checkNotNull(user.uId) { "User id is null" },
                        checkNotNull(channel.channelId) { "Channel id is null" },
                    )
            assertIs<Success<Message>>(newMessageSuccess, "Message creation failed with error")
            assertNotNull(newMessageSuccess.value.msgId, "Message id is null")
            assertEquals(owner.uId, newMessageSuccess.value.user.uId, "User id is different")
            assertEquals(
                channel.channelId,
                newMessageSuccess.value.channel.channelId,
                "Channel id is different",
            )
            assertEquals("Hello, World!", newMessageSuccess.value.msg, "Message is different")
            assertIs<Failure<MessageError.UserDoesNotHaveAccess>>(
                newMessageFailure,
                "Message creation should have failed",
            )
            assertEquals(
                MessageError.UserDoesNotHaveAccess,
                newMessageFailure.value,
                "Message error is different",
            )
        }
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `delete a message`(m: TransactionManager) {
        makeTestEnv(PUBLIC, READ_WRITE, m) { manager, owner, user, channel, messageServices ->
            userJoinChannel(manager, user, channel, READ_WRITE)
            val newMessage =
                messageServices
                    .createMessage(
                        "Hello, World!",
                        checkNotNull(user.uId) { "User id is null" },
                        checkNotNull(channel.channelId) { "Channel id is null" },
                    )
            assertIs<Success<Message>>(newMessage, "Message creation failed")
            assertNotNull(newMessage.value.msgId, "Message id is null")
            val deleteMessage =
                messageServices
                    .deleteMessage(
                        checkNotNull(newMessage.value.msgId) { "Message id is null" },
                        checkNotNull(owner.uId) { "User id is null" },
                    )
            assertIs<Success<Unit>>(deleteMessage, "Message deletion failed")
        }
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `fail to delete a message due to user not having access to message`(m: TransactionManager) {
        makeTestEnv(PUBLIC, READ_WRITE, m) { manager, owner, user, channel, messageServices ->
            userJoinChannel(manager, user, channel, READ_WRITE)
            val newMessage =
                messageServices
                    .createMessage(
                        "Hello, World!",
                        checkNotNull(owner.uId) { "User id is null" },
                        checkNotNull(channel.channelId) { "Channel id is null" },
                    )
            assertIs<Success<Message>>(newMessage, "Message creation failed")
            assertNotNull(newMessage.value.msgId, "Message id is null")
            val deleteMessage =
                messageServices
                    .deleteMessage(
                        checkNotNull(newMessage.value.msgId) { "Message id is null" },
                        checkNotNull(user.uId) { "User id is null" },
                    )
            assertIs<Failure<MessageError.UserDoesNotHaveAccess>>(
                deleteMessage,
                "Message deletion should have failed",
            )
            assertEquals(MessageError.UserDoesNotHaveAccess, deleteMessage.value, "Message error is different")
        }
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `get message`(m: TransactionManager) {
        makeTestEnv(PUBLIC, READ_WRITE, m) { manager, _, user, channel, messageServices ->
            userJoinChannel(manager, user, channel, READ_WRITE)
            val newMessage =
                messageServices
                    .createMessage(
                        "Hello, World!",
                        checkNotNull(user.uId) { "User id is null" },
                        checkNotNull(channel.channelId) { "Channel id is null" },
                    )
            assertIs<Success<Message>>(newMessage, "Message creation failed")
            assertNotNull(newMessage.value.msgId, "Message id is null")
            val getMessage =
                messageServices
                    .getMessage(
                        checkNotNull(newMessage.value.msgId) { "Message id is null" },
                        checkNotNull(user.uId) { "User id is null" },
                    )
            assertIs<Success<Message>>(getMessage, "Message retrieval failed")
            assertEquals(newMessage.value.msgId, getMessage.value.msgId, "Message id is different")
            assertEquals(user.uId, getMessage.value.user.uId, "User id is different")
            assertEquals(channel.channelId, getMessage.value.channel.channelId, "Channel id is different")
            assertEquals("Hello, World!", getMessage.value.msg, "Message is different")
        }
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `fail to get message due to user not in channel`(m: TransactionManager) {
        makeTestEnv(PUBLIC, READ_WRITE, m) { _, owner, user, channel, messageServices ->
            val newMessage =
                messageServices
                    .createMessage(
                        "Hello, World!",
                        checkNotNull(owner.uId) { "User id is null" },
                        checkNotNull(channel.channelId) { "Channel id is null" },
                    )
            assertIs<Success<Message>>(newMessage, "Message creation failed")
            assertNotNull(newMessage.value.msgId, "Message id is null")
            val getMessage =
                messageServices
                    .getMessage(
                        checkNotNull(newMessage.value.msgId) { "Message id is null" },
                        checkNotNull(user.uId) { "User id is null" },
                    )
            assertIs<Failure<MessageError.UserNotInChannel>>(
                getMessage,
                "Message retrieval should have failed",
            )
            assertEquals(MessageError.UserNotInChannel, getMessage.value, "Message error is different")
        }
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `get latest messages`(m: TransactionManager) {
        makeTestEnv(PUBLIC, READ_WRITE, m) { manager, _, user, channel, messageServices ->
            userJoinChannel(manager, user, channel, READ_WRITE)
            repeat(17) {
                val newMessage =
                    messageServices
                        .createMessage(
                            "Hello, World $it!",
                            checkNotNull(user.uId),
                            checkNotNull(channel.channelId),
                        )
                assertIs<Success<Message>>(newMessage, "Message creation failed")
                assertNotNull(newMessage.value.msgId, "Message id is null")
            }
            val messages =
                messageServices
                    .latestMessages(checkNotNull(channel.channelId), checkNotNull(user.uId), 5, 10)
            assertIs<Success<List<Message>>>(messages, "Message retrieval failed")
            assertEquals(10, messages.value.size, "Number of messages is different")
        }
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `fail to get latest messages due to user not in channel`(m: TransactionManager) {
        makeTestEnv(PUBLIC, READ_WRITE, m) { _, owner, user, channel, messageServices ->
            repeat(17) {
                val newMessage =
                    messageServices
                        .createMessage(
                            "Hello, World!",
                            checkNotNull(owner.uId) { "User id is null" },
                            checkNotNull(channel.channelId) { "Channel id is null" },
                        )
                assertIs<Success<Message>>(newMessage, "Message creation failed")
                assertNotNull(newMessage.value.msgId, "Message id is null")
            }
            val messages =
                messageServices
                    .latestMessages(checkNotNull(channel.channelId), checkNotNull(user.uId), 5, 10)
            assertIs<Failure<MessageError.UserNotInChannel>>(messages, "Message retrieval should have failed")
            assertEquals(MessageError.UserNotInChannel, messages.value, "Message error is different")
        }
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `fail to get message due to invalid id`(m: TransactionManager) {
        makeTestEnv(PUBLIC, READ_WRITE, m) { manager, _, user, channel, messageServices ->
            userJoinChannel(manager, user, channel, READ_WRITE)
            val newMessage =
                messageServices
                    .createMessage(
                        "Hello, World!",
                        checkNotNull(user.uId),
                        checkNotNull(channel.channelId),
                    )
            assertIs<Success<Message>>(newMessage, "Message creation failed")
            assertNotNull(newMessage.value.msgId, "Message id is null")
            val getMessage = messageServices.getMessage(123u, checkNotNull(user.uId) { "User id is null" })
            assertIs<Failure<MessageError.MessageNotFound>>(getMessage, "Message retrieval should have failed")
            assertEquals(MessageError.MessageNotFound, getMessage.value, "Message error is different")
        }
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `fail to send a message to non-existent channel`(m: TransactionManager) {
        makeTestEnv(PUBLIC, READ_WRITE, m) { manager, _, user, channel, messageServices ->
            userJoinChannel(manager, user, channel, READ_WRITE)
            val newMessage =
                messageServices
                    .createMessage(
                        "Hello, World!",
                        checkNotNull(user.uId) { "User id is null" },
                        123u,
                    )
            assertIs<Failure<MessageError.ChannelNotFound>>(newMessage, "Message creation should have failed")
            assertEquals(MessageError.ChannelNotFound, newMessage.value, "Message error is different")
        }
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `fail to send a message to non-existent user`(m: TransactionManager) {
        makeTestEnv(PUBLIC, READ_WRITE, m) { _, _, _, channel, messageServices ->
            val newMessage =
                messageServices
                    .createMessage(
                        "Hello, World!",
                        123u,
                        checkNotNull(channel.channelId) { "Channel id is null" },
                    )
            assertIs<Failure<MessageError.UserNotFound>>(newMessage, "Message creation should have failed")
            assertEquals(MessageError.UserNotFound, newMessage.value, "Message error is different")
        }
    }
}