package services

import TransactionManager
import jdbc.transactionManager.TransactionManagerJDBC
import mem.TransactionManagerInMem
import model.channels.AccessControl
import model.channels.Channel
import model.channels.ChannelName
import model.channels.Visibility
import model.messages.Message
import model.users.Password
import model.users.User
import model.users.UserInfo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import utils.Success
import java.sql.Timestamp
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.time.TimeSource

class MessageServicesTest {
    companion object {
        @JvmStatic
        fun transactionManagers(): Stream<TransactionManager> =
            Stream.of(
                TransactionManagerInMem().also { cleanup(it) },
                TransactionManagerJDBC(Environment).also { cleanup(it) },
            )

        private fun cleanup(manager: TransactionManager) =
            manager.run {
                channelRepo.clear()
                userRepo.clear()
                messageRepo.clear()
            }

        private fun makeUser(manager: TransactionManager, username: String ) =
            manager
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
        ) =
            manager
                .run {
                    val user = checkNotNull(user)
                    val userId = checkNotNull(user?.uId) { "User id is null" }
                    val channelId = checkNotNull(channel.channelId) { "Channel id is null" }
                    channelRepo.joinChannel(channelId, userId, accessControl)
                }

        private fun makeChannel(
            manager: TransactionManager,
            user: User,
            accessControl: AccessControl,
            visibility: Visibility,
        ) =
            manager
                .run {
                    val owner = user
                    val ownerId = checkNotNull(owner?.uId) { "Owner id is null" }
                    checkNotNull(owner)
                    channelRepo
                        .createChannel(
                            Channel.createChannel(
                                owner = UserInfo(ownerId, owner.username),
                                name = ChannelName("RWPriv", owner.username),
                                accessControl = accessControl,
                                visibility = visibility,
                            ),
                        )
                }
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `create a new message on a Read_Write channel`(manager: TransactionManager) {
        val user = makeUser(manager,"User")
        val privChannel = makeChannel(
            manager,
            checkNotNull(makeUser(manager,"Owner")) ,
            AccessControl.READ_WRITE,
            Visibility.PRIVATE,
        )
        checkNotNull(privChannel) { "Channel is null" }
        checkNotNull(user) { "User is null" }
        val messageServices = MessageServices(manager)
        userJoinChannel(manager, user, privChannel, AccessControl.READ_WRITE)
        val newMessage = messageServices.createMessage(
            "Hello, World!",
            checkNotNull(user.uId) { "User id is null" },
            checkNotNull(privChannel.channelId) { "Channel id is null" },
            "2024-09-01 12:00:00",
        )
        assertIs<Success<Message>>(newMessage, "Message creation failed with error")
        assertNotNull(newMessage.value.msgId, "Message id is null")
        assertEquals(user.uId, newMessage.value.user.uId, "User id is different")
        assertEquals(privChannel.channelId,
            newMessage.value.channel.channelId,
            "Channel id is different",
        )
        assertEquals("Hello, World!", newMessage.value.msg, "Message is different")
        assertEquals( Timestamp.valueOf("2024-09-01 12:00:00"),
            newMessage.value.creationTime,
            "Creation time is different",
        )
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `delete a message`(manager: TransactionManager) {
        TODO()
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `fail to send a message to non-existent channel`(manager: TransactionManager) {
        TODO()
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `fail to send a message to non-existent user`(manager: TransactionManager) {
        TODO()
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `fail to send an empty message`(manager: TransactionManager) {
        TODO()
    }
}