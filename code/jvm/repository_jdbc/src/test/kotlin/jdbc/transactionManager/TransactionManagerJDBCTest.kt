package jdbc.transactionManager

import jdbc.TestSetup
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
import utils.encryption.DummyEncrypt
import kotlin.test.Test
import kotlin.test.assertNotNull

class TransactionManagerJDBCTest {
    private val validPassword = "Password123"
    private val passwordDefault = Password(validPassword)

    private val transactionManager = TransactionManagerJDBC(TestSetup.dataSource, DummyEncrypt)

    @BeforeEach
    fun clear() {
        transactionManager.run {
            run {
                channelRepo.clear()
                userRepo.clear()
                messageRepo.clear()
            }
        }
    }

    @Test
    fun `test repo interaction`() {
        transactionManager.run {
            val user =
                userRepo
                    .createUser(
                        User(
                            username = "user",
                            password = passwordDefault,
                        ),
                    )
            assertNotNull(user)
            val id = requireNotNull(user.uId)
            val channel =
                channelRepo
                    .createChannel(
                        Channel.createChannel(
                            name = ChannelName("channel", user.username),
                            owner = UserInfo(id, user.username),
                            accessControl = AccessControl.READ_WRITE,
                            visibility = Visibility.PUBLIC,
                        ),
                    )
            assertNotNull(channel)
            val channelId = requireNotNull(channel.cId)
            val message =
                messageRepo
                    .createMessage(
                        Message(
                            msg = "message",
                            user = UserInfo(id, user.username),
                            channel = ChannelInfo(channelId, channel.name),
                        ),
                    )
            assertNotNull(message?.msgId)
        }
    }
}