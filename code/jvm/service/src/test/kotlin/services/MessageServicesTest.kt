package services

import TransactionManager
import jdbc.transactionManager.TransactionManagerJDBC
import mem.TransactionManagerInMem
import model.channels.AccessControl
import model.channels.Channel
import model.channels.ChannelName
import model.channels.Visibility
import model.users.Password
import model.users.User
import model.users.UserInfo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

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
            channel: Channel,
            accessControl: AccessControl,
        ) =
            manager
                .run {
                    val user = makeUser(manager,"User")
                    val userId = checkNotNull(user?.uId) { "User id is null" }
                    val channelId = checkNotNull(channel.channelId) { "Channel id is null" }
                    channelRepo.joinChannel(channelId, userId, accessControl)
                }

        private fun makeChannel(
            manager: TransactionManager,
            accessControl: AccessControl,
            visibility: Visibility,
        ) =
            manager
                .run {
                    val owner = makeUser(manager, "Owner")
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
    fun `create a new message on a private channel`(manager: TransactionManager) {
        val owner = makeUser(manager, "Owner")
        val user = makeUser(manager, "User")
        val privChannel = makeChannel(manager, AccessControl.READ_WRITE, Visibility.PRIVATE)
        checkNotNull(channelRO)
        checkNotNull(channelRW)
        val channelRWId = checkNotNull(channelRW.channelId) { "Channel id is null" }
        val channelROId = checkNotNull(channelRO.channelId) { "Channel id is null" }
        val messageServices = MessageServices(manager)
        userJoinChannel(manager, channelRW, AccessControl.READ_WRITE)
        userJoinChannel(manager, channelRO, AccessControl.READ_ONLY)

    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `delete a message`(manager: TransactionManager) {
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `fail to send a message to non-existent channel`(manager: TransactionManager) {
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `fail to send a message to non-existent user`(manager: TransactionManager) {
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `fail to send an empty message`(manager: TransactionManager) {
    }
}