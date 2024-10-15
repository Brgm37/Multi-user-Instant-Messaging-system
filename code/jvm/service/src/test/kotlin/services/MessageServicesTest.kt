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

        private fun makeOwnerUser(manager: TransactionManager) =
            manager
                .run {
                    userRepo
                        .createUser(
                            User(
                                username = "owner",
                                password = Password("Password123"),
                            ),
                        )
                }

        private fun makeReaderUser(manager: TransactionManager) =
            manager
                .run {
                    userRepo
                        .createUser(
                            User(
                                username = "reader",
                                password = Password("Password1234"),
                            ),
                        )
                }

        private fun makeRWPubChannel(manager: TransactionManager) =
            manager
                .run {
                    val owner = makeOwnerUser(manager)
                    val ownerId = checkNotNull(owner?.uId) { "Owner id is null" }
                    checkNotNull(owner)
                    Channel.createChannel(
                        owner = UserInfo(ownerId,owner.username),
                        name = ChannelName("name",owner.username),
                        accessControl = AccessControl.READ_WRITE,
                        visibility =  Visibility.PUBLIC
                    )
                }

        private fun makeROPubChannel(manager: TransactionManager) =
            manager
                .run {
                    val owner = makeOwnerUser(manager)
                    val ownerId = checkNotNull(owner?.uId) { "Owner id is null" }
                    checkNotNull(owner)
                    Channel.createChannel(
                        owner = UserInfo(ownerId,owner.username),
                        name = ChannelName("name",owner.username),
                        accessControl = AccessControl.READ_ONLY,
                        visibility =  Visibility.PUBLIC
                    )
                }

        private fun makeRWPrivChannel(manager: TransactionManager) =
            manager
                .run {
                    val owner = makeOwnerUser(manager)
                    val ownerId = checkNotNull(owner?.uId) { "Owner id is null" }
                    checkNotNull(owner)
                    Channel.createChannel(
                        owner = UserInfo(ownerId,owner.username),
                        name = ChannelName("name",owner.username),
                        accessControl = AccessControl.READ_WRITE,
                        visibility =  Visibility.PRIVATE
                    )
                }
        private fun makeROPrivChannel(manager: TransactionManager) =
            manager
                .run {
                    val owner = makeOwnerUser(manager)
                    val ownerId = checkNotNull(owner?.uId) { "Owner id is null" }
                    checkNotNull(owner)
                    Channel.createChannel(
                        owner = UserInfo(ownerId,owner.username),
                        name = ChannelName("name",owner.username),
                        accessControl = AccessControl.READ_ONLY,
                        visibility =  Visibility.PRIVATE
                    )
                }
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `create a new message while being the channel owner`(manager: TransactionManager) {
        val owner = makeOwnerUser(manager)

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