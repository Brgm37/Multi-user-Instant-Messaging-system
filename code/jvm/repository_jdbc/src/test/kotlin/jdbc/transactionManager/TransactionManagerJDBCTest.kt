package jdbc.transactionManager

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import jdbc.transactionManager.dataSource.ConnectionSource
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
import kotlin.test.Test
import kotlin.test.assertNotNull

class TransactionManagerJDBCTest {
    private val validPassword = "Password123"
    private val passwordDefault = Password(validPassword)

    private object TestConnectionSource : ConnectionSource {
        override val connectionUrl: String
            get() = "jdbc:postgresql://localhost:5433/daw_test"
        override val username: String
            get() = "postgres"
        override val password: String
            get() = "password"
        override val poolSize: Int
            get() = 10
    }

    private val hikari =
        HikariConfig()
            .apply {
                jdbcUrl = TestConnectionSource.connectionUrl
                username = TestConnectionSource.username
                password = TestConnectionSource.password
                maximumPoolSize = TestConnectionSource.poolSize
            }.let { HikariDataSource(it) }

    private val transactionManager = TransactionManagerJDBC(hikari)

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
            val channelId = requireNotNull(channel.channelId)
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