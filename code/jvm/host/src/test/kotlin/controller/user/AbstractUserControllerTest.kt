package controller.user

import TransactionManager
import com.example.appWeb.controller.UserController
import controller.TestConfig
import model.users.Password
import model.users.User
import model.users.UserInvitation
import model.users.UserToken
import org.example.appWeb.HttpApiApplication
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import java.sql.Timestamp
import java.time.LocalDate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [TestConfig::class, HttpApiApplication::class],
)
abstract class AbstractUserControllerTest {
    @LocalServerPort
    var port: Int = 0

    @Autowired
    private lateinit var manager: TransactionManager

    private fun makeUserName(): () -> String {
        var count = 0
        return {
            count++
            "user$count"
        }
    }

    private val usernameMaker = makeUserName()

    private val username: String
        get() {
            return usernameMaker()
        }

    private lateinit var token: UserToken

    @BeforeAll
    fun setUp() {
        manager.run {
            channelRepo.clear()
            userRepo.clear()
            messageRepo.clear()
            val user =
                userRepo
                    .createUser(
                        User(
                            username = "owner",
                            password = Password("Password123"),
                        ),
                    )
                    ?: throw IllegalStateException("User not created")
            userRepo.save(user)
            val uId = checkNotNull(user.uId) { "User not created" }
            token = UserToken(uId)
            userRepo.createToken(token)
        }
    }

    @Test
    fun `sign up`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val invitation =
            manager
                .run {
                    val user =
                        userRepo.findByToken(token.token.toString()) ?: throw IllegalStateException("User not found")
                    val uId = checkNotNull(user.uId) { "User not found" }
                    val userInvitation =
                        UserInvitation(
                            uId,
                            Timestamp.valueOf(LocalDate.now().plusDays(1).atStartOfDay()),
                        )
                    userRepo.createInvitation(userInvitation)
                    userInvitation
                }

        client
            .post()
            .uri(UserController.USER_BASE_URL + UserController.SIGNUP_URL)
            .header("Authorization", "Bearer ${token.token}")
            .bodyValue(
                mapOf(
                    "username" to "username",
                    "password" to "Password123",
                    "invitationCode" to invitation.invitationCode,
                    "inviterUId" to invitation.inviterId,
                ),
            ).exchange()
            .expectStatus()
            .isOk
    }
}