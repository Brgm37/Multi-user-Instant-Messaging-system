package controller.channel

import TransactionManager
import controller.TestConfig
import model.users.Password
import model.users.User
import org.example.appWeb.HttpApiApplication
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [TestConfig::class, HttpApiApplication::class],
)
abstract class AbstractChannelControllerTest {
    @LocalServerPort
    var port: Int = 0

    @Autowired
    private lateinit var manager: TransactionManager

    private lateinit var user: User

    @BeforeAll
    fun setUp() {
        manager.run {
            channelRepo.clear()
            userRepo.clear()
            messageRepo.clear()
            user =
                userRepo
                    .createUser(
                        User(
                            username = "owner",
                            password = Password("Password123"),
                        ),
                    )
                    ?: throw IllegalStateException("User not created")
            userRepo.save(user)
        }
    }

    @Test
    fun `create a channel`() {
//        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
//
//        client
//            .post()
//            .uri(ChannelController.CHANNEL_BASE_URL)
//            .header("Authorization", "Bearer ${user.token}")
//            .bodyValue(
//                mapOf(
//                    "name" to "channel",
//                    "accessControl" to READ_WRITE.name,
//                    "visibility" to PUBLIC.name,
//                ),
//            ).exchange()
//            .expectStatus()
//            .isOk
    }
}