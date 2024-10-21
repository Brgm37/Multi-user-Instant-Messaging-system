package com.example.appWeb.controller

import TransactionManager
import com.example.appWeb.model.dto.input.message.CreateMessageInputModel
import com.example.appWeb.model.dto.input.user.AuthenticatedUserInputModel
import com.example.appWeb.model.dto.output.message.MessageOutputModel
import com.example.appWeb.model.problem.ChannelProblem
import com.example.appWeb.model.problem.MessageProblem
import com.example.appWeb.model.problem.UserProblem
import jdbc.transactionManager.TransactionManagerJDBC
import mem.TransactionManagerInMem
import model.channels.AccessControl.READ_WRITE
import model.channels.Channel
import model.channels.ChannelName
import model.channels.Visibility.PUBLIC
import model.messages.Message
import model.users.Password
import model.users.User
import model.users.UserInfo
import model.users.UserToken
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.HttpStatus
import services.MessageServices
import utils.Success
import utils.encryption.DummyEncrypt
import java.util.UUID
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertIs

class MessageControllerTest {
    companion object {
        @JvmStatic
        fun transactionManager(): Stream<TransactionManager> =
            Stream.of(
                TransactionManagerInMem().also { cleanup(it) },
                TransactionManagerJDBC(TestSetup.dataSource, DummyEncrypt).also { cleanup(it) },
            )

        private fun cleanup(manager: TransactionManager) {
            manager.run {
                channelRepo.clear()
                messageRepo.clear()
                userRepo.clear()
            }
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
            user: AuthenticatedUserInputModel,
            channel: Channel,
        ) = manager
            .run {
                val userId = checkNotNull(user.uId) { "User id is null" }
                val channelId = checkNotNull(channel.cId) { "Channel id is null" }
                channelRepo.joinChannel(channelId, userId, READ_WRITE)
            }

        private fun makeChannel(
            manager: TransactionManager,
            user: User,
        ) = manager.run {
            val userId = checkNotNull(user.uId)
            channelRepo
                .createChannel(
                    Channel.createChannel(
                        owner = UserInfo(userId, user.username),
                        name = ChannelName("Channel", user.username),
                        accessControl = READ_WRITE,
                        visibility = PUBLIC,
                    ),
                )
        }

        private fun makeToken(
            manager: TransactionManager,
            uId: UInt,
        ) = manager
            .run {
                val token = UserToken(userId = uId, token = UUID.randomUUID())
                userRepo
                    .createToken(token)
                token
            }

        private fun makeTest(
            manager: TransactionManager,
            block: MessageController
            .(
                TransactionManager,
                AuthenticatedUserInputModel,
                AuthenticatedUserInputModel,
                Channel,
                MessageServices,
            ) -> Unit,
        ) {
            val messageServices = MessageServices(manager)
            val owner = makeUser(manager, username)
            val user = checkNotNull(makeUser(manager, username))
            val userId = checkNotNull(user.uId) { "User id is null" }
            val ownerId = checkNotNull(owner?.uId) { "Owner id is null" }
            val authUser = AuthenticatedUserInputModel(userId, makeToken(manager, userId).token.toString())
            val authOwner = AuthenticatedUserInputModel(ownerId, makeToken(manager, ownerId).token.toString())
            val channel = makeChannel(manager, checkNotNull(owner))
            checkNotNull(channel) { "Channel is null" }
            val messageController = MessageController(messageServices)
            messageController.block(manager, authOwner, authUser, channel, messageServices)
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
    }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `create message`(m: TransactionManager) =
        makeTest(m) { manager, _, user, channel, _ ->
            userJoinChannel(manager, user, channel)
            createMessage(
                CreateMessageInputModel(
                    "Hello, World!",
                    checkNotNull(channel.cId),
                ),
                user,
            ).let { resp ->
                assertEquals(HttpStatus.OK, resp.statusCode, "Status code is not OK")
                assertIs<MessageOutputModel>(resp.body, "Response body is not a MessageOutputModel")
                val message = resp.body as MessageOutputModel
                assertEquals("Hello, World!", message.message, "Message is not correct")
                assertEquals(user.uId, message.user, "User is not correct")
                assertEquals(channel.cId, message.channel, "Channel is not correct")
            }
        }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `fail to create message due o invalid info`(m: TransactionManager) =
        makeTest(m) { _, owner, _, channel, _ ->
            createMessage(
                CreateMessageInputModel(
                    "",
                    checkNotNull(channel.cId),
                ),
                owner,
            ).let { resp ->
                assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode, "Status code is not BAD_REQUEST")
                assertIs<MessageProblem.InvalidMessageInfo>(resp.body, "Body is not a InvalidMessageInfo")
            }
        }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `fail to create message due to user not found`(m: TransactionManager) =
        makeTest(m) { _, _, _, channel, _ ->
            createMessage(
                CreateMessageInputModel(
                    "Hello, World!",
                    checkNotNull(channel.cId),
                ),
                AuthenticatedUserInputModel(0u, "token"),
            ).let { resp ->
                assertEquals(HttpStatus.NOT_FOUND, resp.statusCode, "Status code is different")
                assertIs<UserProblem.UserNotFound>(resp.body, "Body is not a UserNotFound")
            }
        }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `fail to create message due to channel not found`(m: TransactionManager) {
        val owner = AuthenticatedUserInputModel(0u, "token")
        val messageServices = MessageServices(m)
        val channelController = MessageController(messageServices)
        channelController
            .createMessage(
                CreateMessageInputModel(
                    "Hello, World!",
                    0u,
                ),
                owner,
            ).let { resp ->
                assertEquals(HttpStatus.NOT_FOUND, resp.statusCode, "Status code is different")
                assertIs<ChannelProblem.ChannelNotFound>(resp.body, "Body is not a UserNotFound")
            }
    }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `get a message`(m: TransactionManager) =
        makeTest(m) { manager, owner, user, channel, messageServices ->
            userJoinChannel(manager, user, channel)
            val message =
                messageServices.createMessage(
                    "Hello, World!",
                    checkNotNull(owner.uId),
                    checkNotNull(channel.cId),
                )
            assertIs<Success<Message>>(message, "Message was not created")
            val msgId = checkNotNull(message.value.msgId)
            getSingleMessage(msgId, user).let { resp ->
                assertEquals(HttpStatus.OK, resp.statusCode, "Status code is not OK")
                assertIs<MessageOutputModel>(resp.body, "Response body is not a MessageOutputModel")
                val msgOutputModel = resp.body as MessageOutputModel
                assertEquals(msgId, msgOutputModel.id, "Message id is not correct")
                assertEquals("Hello, World!", msgOutputModel.message, "Message is not correct")
                assertEquals(owner.uId, msgOutputModel.user, "User is not correct")
                assertEquals(channel.cId, msgOutputModel.channel, "Channel is not correct")
            }
        }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `fail to get a message`(m: TransactionManager) =
        makeTest(m) { _, _, user, _, _ ->
            getSingleMessage(0u, user).let { resp ->
                assertEquals(HttpStatus.NOT_FOUND, resp.statusCode, "Status code is not NOT_FOUND")
                assertIs<MessageProblem.MessageNotFound>(resp.body, "Body is not a MessageNotFound")
            }
        }

    @ParameterizedTest
    @MethodSource("transactionManager")
    @Suppress("UNCHECKED_CAST")
    fun `get channel messages`(m: TransactionManager) =
        makeTest(m) { manager, owner, user, channel, messageServices ->
            val nr = 20
            userJoinChannel(manager, user, channel)
            repeat(nr) {
                val message =
                    messageServices.createMessage(
                        "Hello, World! $it",
                        checkNotNull(owner.uId),
                        checkNotNull(channel.cId),
                    )
                assertIs<Success<Message>>(message, "Message was not created")
            }
            getChannelMessages(checkNotNull(channel.cId), 5u, 10u, user).let { resp ->
                assertEquals(HttpStatus.OK, resp.statusCode, "Status code is not OK")
                assertIs<List<MessageOutputModel>>(
                    resp.body,
                    "Response body is not a List<MessageOutputModel>",
                )
                val messages = resp.body as List<MessageOutputModel>
                assertEquals(10, messages.size, "Number of messages is not correct")
                messages.forEachIndexed { index, message ->
                    assertEquals(
                        "Hello, World! ${14 - index}",
                        message.message,
                        "Message is not correct",
                    )
                    assertEquals(owner.uId, message.user, "User is not correct")
                    assertEquals(channel.cId, message.channel, "Channel is not correct")
                }
            }
        }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `fail to get channel messages`(m: TransactionManager) =
        makeTest(m) { _, _, user, channel, _ ->
            getChannelMessages(checkNotNull(channel.cId), 0u, 10u, user).let { resp ->
                assertEquals(HttpStatus.NOT_FOUND, resp.statusCode, "Status code is not NOT_FOUND")
                assertIs<MessageProblem.MessageNotFound>(resp.body, "Body is not a MessageNotFound")
            }
        }
}