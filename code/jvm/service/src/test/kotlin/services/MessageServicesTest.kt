package services

import TransactionManager
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import errors.MessageError
import jdbc.transactionManager.TransactionManagerJDBC
import mem.TransactionManagerInMem
import model.channels.AccessControl
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
import java.sql.Timestamp
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class MessageServicesTest {
    companion object {
        private val hikari =
            HikariConfig()
                .apply {
                    jdbcUrl = Environment.connectionUrl
                    username = Environment.username
                    password = Environment.password
                    maximumPoolSize = Environment.poolSize
                }.let { HikariDataSource(it) }

        @JvmStatic
        fun transactionManagers(): Stream<TransactionManager> =
            Stream.of(
                TransactionManagerInMem().also { cleanup(it) },
                TransactionManagerJDBC(hikari).also { cleanup(it) },
            )

        private fun cleanup(manager: TransactionManager) =
            manager.run {
                channelRepo.clear()
                userRepo.clear()
                messageRepo.clear()
            }

        private fun makeUser(
            manager: TransactionManager,
            username: String = "Owner",
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
            accessControl: AccessControl = READ_WRITE,
            visibility: Visibility = PRIVATE,
        ) = manager
            .run {
                val ownerId = checkNotNull(user.uId) { "Owner id is null" }
                channelRepo
                    .createChannel(
                        Channel.createChannel(
                            owner = UserInfo(ownerId, user.username),
                            name = ChannelName("RWPriv", user.username),
                            accessControl = accessControl,
                            visibility = visibility,
                        ),
                    )
            }
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `create a new message`(manager: TransactionManager) {
        val user = makeUser(manager, "Writer")
        val channel =
            makeChannel(
                manager,
                checkNotNull(makeUser(manager)) { "Owner is null" },
                visibility = PUBLIC,
            )
        checkNotNull(channel) { "Channel is null" }
        checkNotNull(user) { "User is null" }
        val messageServices = MessageServices(manager)
        userJoinChannel(manager, user, channel, READ_WRITE)
        val newMessage =
            messageServices
                .createMessage(
                    "Hello, World!",
                    checkNotNull(user.uId) { "User id is null" },
                    checkNotNull(channel.channelId) { "Channel id is null" },
                    "2024-09-01 12:00:00",
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
        assertEquals(
            Timestamp.valueOf("2024-09-01 12:00:00"),
            newMessage.value.creationTime,
            "Creation time is different",
        )
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `fail to create a new message due to blank message`(manager: TransactionManager) {
        val user = makeUser(manager, "Writer")
        val channel =
            makeChannel(
                manager,
                checkNotNull(makeUser(manager)) { "Owner is null" },
            )
        checkNotNull(channel) { "Channel is null" }
        checkNotNull(user) { "User is null" }
        val messageServices = MessageServices(manager)
        userJoinChannel(manager, user, channel, READ_WRITE)
        val newMessage =
            messageServices
                .createMessage(
                    "",
                    checkNotNull(user.uId) { "User id is null" },
                    checkNotNull(channel.channelId) { "Channel id is null" },
                    "2024-09-01 12:00:00",
                )
        assertIs<Failure<MessageError.InvalidMessageInfo>>(newMessage, "Message creation should have failed")
        assertEquals(MessageError.InvalidMessageInfo, newMessage.value, "Message error is different")
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `fail to create message due to user not belonging to the channel`(manager: TransactionManager) {
        val user = makeUser(manager, "Writer")
        val channel =
            makeChannel(
                manager,
                checkNotNull(makeUser(manager)) { "Owner is null" },
                visibility = PUBLIC,
            )
        checkNotNull(channel) { "Channel is null" }
        checkNotNull(user) { "User is null" }
        val messageServices = MessageServices(manager)
        val newMessage =
            messageServices
                .createMessage(
                    "Hello, World!",
                    checkNotNull(user.uId) { "User id is null" },
                    checkNotNull(channel.channelId) { "Channel id is null" },
                    "2024-09-01 12:00:00",
                )
        assertIs<Failure<MessageError.UserNotInChannel>>(newMessage, "Message creation should have failed")
        assertEquals(MessageError.UserNotInChannel, newMessage.value, "Message error is different")
    }

//    @ParameterizedTest
//    @MethodSource("transactionManagers")
//    fun `Public channel READ_ONLY message creation success and fail due to access control`(manager: TransactionManager) {
//        val user = checkNotNull(makeUser(manager,"User")) { "User is null" }
//        val owner = checkNotNull(makeUser(manager)) { "Owner is null" }
//        val channel = makeChannel(
//            manager,
//            owner,
//            accessControl = READ_ONLY,
//            visibility = PUBLIC,
//        )
//        checkNotNull(channel) { "Channel is null" }
//        val messageServices = MessageServices(manager)
//        userJoinChannel(manager, user, channel, READ_WRITE)
//        val newMessageFailure = messageServices.createMessage(
//            "Hello, World!",
//            checkNotNull(user.uId) { "User id is null" },
//            checkNotNull(channel.channelId) { "Channel id is null" },
//            "2024-09-01 12:00:00",
//        )
//        val newMessageSuccess = messageServices.createMessage(
//            "Hello, World!",
//            checkNotNull(owner.uId) { "Owner id is null" },
//            checkNotNull(channel.channelId) { "Channel id is null" },
//            "2024-09-01 12:00:01",
//        )
//        assertIs<Success<Message>>(newMessageSuccess, "Message creation failed with error")
//        assertNotNull(newMessageSuccess.value.msgId, "Message id is null")
//        assertEquals(owner.uId, newMessageSuccess.value.user.uId, "User id is different")
//        assertEquals(channel.channelId,
//            newMessageSuccess.value.channel.channelId,
//            "Channel id is different",
//        )
//        assertEquals("Hello, World!", newMessageSuccess.value.msg, "Message is different")
//        assertEquals( Timestamp.valueOf("2024-09-01 12:00:01"),
//           newMessageSuccess.value.creationTime,
//            "Creation time is different",
//        )
//        assertIs<Failure<UserDoesNotHaveAccess>>(newMessageSuccess, "Message creation should have failed")
//        assertEquals(UserDoesNotHaveAccess, newMessageSuccess.value, "Message error is different")
//        assertIs<Failure<UserDoesNotHaveAccess>>(newMessageFailure, "Message creation should have failed")
//        assertEquals(UserDoesNotHaveAccess, newMessageFailure.value, "Message error is different")
//    }

//    @ParameterizedTest
//    @MethodSource("transactionManagers")
//    fun `Private channel message creation success and fail due to access control`(manager: TransactionManager) {
//        val user = checkNotNull(makeUser(manager,"Writer")) { "Writer is null" }
//        val reader = checkNotNull(makeUser(manager, "Reader")) { "Reader is null" }
//        val channel = makeChannel(
//            manager,
//            checkNotNull(makeUser(manager)),
//        )
//        checkNotNull(channel) { "Channel is null" }
//        val messageServices = MessageServices(manager)
//        userJoinChannel(manager, user, channel, READ_WRITE)
//        userJoinChannel(manager, reader, channel, READ_ONLY)
//        val newMessageSuccess = messageServices.createMessage(
//            "Hello, World!",
//            checkNotNull(user.uId) { "User id is null" },
//            checkNotNull(channel.channelId) { "Channel id is null" },
//            "2024-09-01 12:00:00",
//        )
//        val newMessageFailure = messageServices.createMessage(
//            "Hello, World!",
//            checkNotNull(reader.uId) { "Owner id is null" },
//            checkNotNull(channel.channelId) { "Channel id is null" },
//            "2024-09-01 12:00:01",
//        )
//        assertIs<Success<Message>>(newMessageSuccess, "Message creation failed with error")
//        assertNotNull(newMessageSuccess.value.msgId, "Message id is null")
//        assertEquals(user.uId, newMessageSuccess.value.user.uId, "User id is different")
//        assertEquals(channel.channelId,
//            newMessageSuccess.value.channel.channelId,
//            "Channel id is different",
//        )
//        assertEquals("Hello, World!", newMessageSuccess.value.msg, "Message is different")
//        assertEquals( Timestamp.valueOf("2024-09-01 12:00:00"),
//            newMessageSuccess.value.creationTime,
//            "Creation time is different",
//        )
//        assertIs<Failure<UserDoesNotHaveAccess>>(newMessageFailure, "Message creation should have failed")
//        assertEquals(UserDoesNotHaveAccess, newMessageFailure.value, "Message error is different")
//    }

//    @ParameterizedTest
//    @MethodSource("transactionManagers")
//    fun `delete a message`(manager: TransactionManager) {
//        val user = checkNotNull(makeUser(manager,"Writer"))
//        val uId = checkNotNull(user.uId) { "User id is null" }
//        val channel = makeChannel(
//            manager,
//            checkNotNull(makeUser(manager)) { "Owner is null" },
//        )
//        checkNotNull(channel) { "Channel is null" }
//        val channelId = checkNotNull(channel.channelId) { "Channel is null" }
//        userJoinChannel(manager, user, channel, READ_WRITE)
//        val messageServices = MessageServices(manager)
//        val newMessage = messageServices.createMessage(
//            "Hello, World!",
//            uId,
//            channelId,
//            "2024-09-01 12:00:00",
//        )
//        assertIs<Success<Message>>(newMessage, "Message creation failed")
//        assertNotNull(newMessage.value.msgId, "Message id is null")
//        val newMsgId = checkNotNull(newMessage.value.msgId) { "Message id is null" }
//        val deleteMessage = messageServices.deleteMessage(
//            newMsgId,
//            uId,
//        )
//        assertIs<Success<Unit>>(deleteMessage, "Message deletion failed ")
//    }

//    @ParameterizedTest
//    @MethodSource("transactionManagers")
//    fun `get message`(manager: TransactionManager) {
//        val user = checkNotNull(makeUser(manager,"Writer"))
//        val uId = checkNotNull(user.uId) { "User id is null" }
//        val channel = makeChannel(
//            manager,
//            checkNotNull(makeUser(manager)) { "Owner is null" },
//        )
//        checkNotNull(channel) { "Channel is null" }
//        val channelId = checkNotNull(channel.channelId) { "Channel is null" }
//        userJoinChannel(manager, user, channel, READ_WRITE)
//        val messageServices = MessageServices(manager)
//        val newMessage = messageServices.createMessage(
//            "Hello, World!",
//            uId,
//            channelId,
//            "2024-09-01 12:00:00",
//        )
//        assertIs<Success<Message>>(newMessage, "Message creation failed")
//        assertNotNull(newMessage.value.msgId, "Message id is null")
//        val newMsgId = checkNotNull(newMessage.value.msgId) { "Message id is null" }
//        val getMessage = messageServices.getMessage(newMsgId, uId)
//        assertIs<Success<Message>>(getMessage, "Message retrieval failed")
//        assertEquals(newMsgId, getMessage.value.msgId, "Message id is different")
//        assertEquals(uId, getMessage.value.user.uId, "User id is different")
//        assertEquals(channelId, getMessage.value.channel.channelId, "Channel id is different")
//        assertEquals("Hello, World!", getMessage.value.msg, "Message is different")
//        assertEquals(Timestamp.valueOf("2024-09-01 12:00:00"), getMessage.value.creationTime, "Creation time is different")
//    }

//    @ParameterizedTest
//    @MethodSource("transactionManagers")
//    fun `get messages`(manager: TransactionManager) {
//        val user = checkNotNull(makeUser(manager,"Writer"))
//        val uId = checkNotNull(user.uId) { "User id is null" }
//        val channel = makeChannel(
//            manager,
//            checkNotNull(makeUser(manager)) { "Owner is null" },
//        )
//        checkNotNull(channel) { "Channel is null" }
//        val channelId = checkNotNull(channel.channelId) { "Channel is null" }
//        userJoinChannel(manager, user, channel, READ_WRITE)
//        val messageServices = MessageServices(manager)
//        val nr = 20
//        repeat(nr){
//            val newMessage = messageServices.createMessage(
//                "Hello, World $nr!",
//                uId,
//                channelId,
//                "2024-09-01 12:00:00",
//            )
//            assertIs<Success<Message>>(newMessage, "Message creation failed")
//            assertNotNull(newMessage.value.msgId, "Message id is null")
//        }
//        for (i in 5 until 10) {
//           val message = messageServices.getMessage(i.toUInt(), uId)
//            assertIs<Success<Message>>(message, "Message retrieval failed")
//            assertEquals(i.toUInt(), message.value.msgId, "Message id is different")
//            assertEquals(uId, message.value.user.uId, "User id is different")
//            assertEquals(channelId, message.value.channel.channelId, "Channel id is different")
//            assertEquals("Hello, World $i!", message.value.msg, "Message is different")
//            assertEquals(Timestamp.valueOf("2024-09-01 12:00:00"), message.value.creationTime, "Creation time is different")
//        }
//    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `fail to get message due to invalid id`(manager: TransactionManager) {
        val user = checkNotNull(makeUser(manager, "Writer"))
        val uId = checkNotNull(user.uId) { "User id is null" }
        val channel =
            makeChannel(
                manager,
                checkNotNull(makeUser(manager)) { "Owner is null" },
            )
        checkNotNull(channel) { "Channel is null" }
        val channelId = checkNotNull(channel.channelId) { "Channel is null" }
        userJoinChannel(manager, user, channel, READ_WRITE)
        val messageServices = MessageServices(manager)
        val newMessage =
            messageServices
                .createMessage(
                    "Hello, World!",
                    uId,
                    channelId,
                    "2024-09-01 12:00:00",
                )
        assertIs<Success<Message>>(newMessage, "Message creation failed")
        assertNotNull(newMessage.value.msgId, "Message id is null")
        val getMessage = messageServices.getMessage(123u, uId)
        assertIs<Failure<MessageError.MessageNotFound>>(getMessage, "Message retrieval should have failed")
        assertEquals(MessageError.MessageNotFound, getMessage.value, "Message error is different")
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `fail to send a message to non-existent channel`(manager: TransactionManager) {
        val user = checkNotNull(makeUser(manager, "Writer"))
        val uId = checkNotNull(user.uId) { "User id is null" }
        val newMessage =
            MessageServices(manager)
                .createMessage(
                    "Hello, World!",
                    uId,
                    123u,
                    "2024-09-01 12:00:00",
                )
        assertIs<Failure<MessageError.ChannelNotFound>>(newMessage, "Message creation should have failed")
        assertEquals(MessageError.ChannelNotFound, newMessage.value, "Message error is different")
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `fail to send a message to non-existent user`(manager: TransactionManager) {
        val channel =
            makeChannel(
                manager,
                checkNotNull(makeUser(manager)) { "Owner is null" },
            )
        checkNotNull(channel) { "Channel is null" }
        val newMessage =
            MessageServices(manager)
                .createMessage(
                    "Hello, World!",
                    123u,
                    checkNotNull(channel.channelId) { "Channel id is null" },
                    "2024-09-01 12:00:00",
                )
        assertIs<Failure<MessageError.UserNotFound>>(newMessage, "Message creation should have failed")
        assertEquals(MessageError.UserNotFound, newMessage.value, "Message error is different")
    }
}