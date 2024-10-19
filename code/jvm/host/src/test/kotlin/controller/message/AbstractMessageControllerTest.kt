package controller.message

import TransactionManager
import com.example.appWeb.controller.MessageController.Companion.MESSAGE_CREATE_URL
import controller.TestConfig
import model.channels.AccessControl
import model.channels.Channel
import model.channels.ChannelInfo
import model.channels.ChannelName
import model.channels.Visibility
import model.messages.Message
import model.users.Password
import model.users.User
import model.users.UserInfo
import model.users.UserToken
import org.example.appWeb.HttpApiApplication
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.Test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [TestConfig::class, HttpApiApplication::class],
)
abstract class AbstractMessageControllerTest {
    @LocalServerPort
    var port: Int = 0

    @Autowired
    private lateinit var manager: TransactionManager

    private lateinit var token: UserToken

    private var chId: UInt = 0U

    private var usId: UInt = 0U

    @BeforeAll
    fun setup() {
        manager.run {
            channelRepo.clear()
            userRepo.clear()
            messageRepo.clear()
            val owner =
                userRepo
                    .createUser(
                        User(
                            username = "owner",
                            password = Password("Password123"),
                        ),
                    )
                    ?: throw IllegalStateException("User not created")
            userRepo.save(owner)
            val uId = checkNotNull(owner.uId) { "User not created" }
            usId = uId
            token = UserToken(uId)
            userRepo.createToken(token)
            val channel =
                channelRepo
                    .createChannel(
                        Channel.createChannel(
                            owner = UserInfo(uId, owner.username),
                            name = ChannelName("channel", owner.username),
                            accessControl = AccessControl.READ_WRITE,
                            visibility = Visibility.PUBLIC,
                        ),
                    )
                    ?: throw IllegalStateException("Channel not created")
            channelRepo.save(channel)
            val channelId = checkNotNull(channel.cId) { "Channel not created" }
            chId = channelId
        }
    }

    @Test
    fun `create message`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        client
            .post()
            .uri(MESSAGE_CREATE_URL)
            .header("Authorization", "Bearer ${token.token}")
            .bodyValue(
                mapOf(
                    "msg" to "Hello, World!",
                    "channel" to chId,
                ),
            ).exchange()
            .expectStatus()
            .isOk
    }

    @Test
    fun `fail to create message due to invalid token`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        client
            .post()
            .uri(MESSAGE_CREATE_URL)
            .header("Authorization", "Bearer invalid")
            .bodyValue(
                mapOf(
                    "msg" to "Hello, World!",
                    "channel" to chId,
                ),
            ).exchange()
            .expectStatus()
            .isUnauthorized
    }

    @Test
    fun `fail to create message due to invalid channel`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        client
            .post()
            .uri(MESSAGE_CREATE_URL)
            .bodyValue(
                mapOf(
                    "msg" to "Hello, World!",
                    "channel" to 0,
                ),
            ).exchange()
            .expectStatus()
            .isUnauthorized
    }

    @Test
    fun `get single message`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val message =
            manager
                .run {
                    messageRepo
                        .createMessage(
                            Message(
                                msg = "Hello, World!",
                                user = UserInfo(usId, "owner"),
                                channel =
                                    ChannelInfo(
                                        chId,
                                        ChannelName(
                                            "channel",
                                            "owner",
                                        ),
                                    ),
                            ),
                        )
                        ?: throw IllegalStateException("Message not created")
                }

        val messageId = checkNotNull(message.msgId) { "Message not created" }

        client
            .get()
            .uri("$MESSAGE_CREATE_URL/$messageId")
            .header("Authorization", "Bearer ${token.token}")
            .exchange()
            .expectStatus()
            .isOk
    }

//    @Test
//    fun `get channel messages`() {
//        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
//            repeat(10) {
//                manager
//                    .run {
//                        messageRepo
//                            .createMessage(
//                                Message(
//                                    msg = "Hello, World!",
//                                    user = UserInfo(usId, "owner"),
//                                    channel = ChannelInfo(chId, ChannelName(
//                                        "channel",
//                                        "owner",
//                                        )
//                                    ),
//                                ),
//                            )
//                            ?: throw IllegalStateException("Message not created")
//                    }
//            }
//        client
//            .get()
//            .uri("$CHANNEL_BASE_URL/$chId/messages")
//            .header("Authorization", "Bearer ${token.token}")
//            .exchange()
//            .expectStatus()
//            .isOk
//    }
}