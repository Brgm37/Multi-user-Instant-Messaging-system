package controller.message

import TransactionManager
import controller.TestConfig
import model.channels.AccessControl
import model.channels.Channel
import model.channels.ChannelName
import model.channels.Visibility
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
            token = UserToken(uId)
            userRepo.createToken(token)
            val channel =
                channelRepo
                    .createChannel(
                        Channel.createChannel(
                            owner = UserInfo(uId, owner.username),
                            name = ChannelName("channel", owner.username),
                            accessControl = AccessControl.READ_WRITE,
                            visibility = Visibility.PUBLIC
                        ),
                    )
        }
    }

    @Test
    fun `create message`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        client
            .post()
            .uri("/message")
            .header("Authorization", "Bearer ${token.token}")
            .bodyValue(
                """
                {
                    "content": "Hello, World!",
                    "channelId": "1"
                }
                """.trimIndent()
            )
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("$.content").isEqualTo("Hello, World!")
            .jsonPath("$.channelId").isEqualTo("1")
    }
}