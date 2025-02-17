package controller.user

import TransactionManager
import com.example.appWeb.controller.UserController
import controller.TestConfig
import model.users.Password
import model.users.User
import model.users.UserInvitation
import model.users.UserToken
import org.example.appWeb.HttpApiApplication
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val VALID_USERNAME = "username"
private const val VALID_PASSWORD = "Password123"
private const val INVALID_PASSWORD = "password"
private const val INVITER_USERNAME = "owner"
private const val NON_EXISTENT_USERNAME = "nonexistent"

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

    private lateinit var token: UserToken

    private fun userInvitation(
        expirationDate: Timestamp = Timestamp.valueOf(LocalDate.now().plusDays(1).atStartOfDay()),
    ): UserInvitation {
        val invitation =
            manager
                .run {
                    val user =
                        userRepo.findToken(token.token.toString()) ?: throw IllegalStateException("User not found")
                    val uId = checkNotNull(user.uId) { "User not found" }
                    val userInvitation =
                        UserInvitation(
                            uId,
                            expirationDate,
                        )
                    userRepo.createInvitation(userInvitation)
                    userInvitation
                }
        return invitation
    }

    @BeforeEach
    fun setUp() {
        manager.run {
            channelRepo.clear()
            userRepo.clear()
            messageRepo.clear()
            val user =
                userRepo
                    .createUser(
                        User(
                            username = INVITER_USERNAME,
                            password = Password(VALID_PASSWORD),
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
    fun `signing up with valid body should return OK and userId`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val invitation =
            userInvitation()

        client
            .post()
            .uri(UserController.USER_BASE_URL + UserController.SIGNUP_URL)
            .bodyValue(
                mapOf(
                    "username" to VALID_USERNAME,
                    "password" to VALID_PASSWORD,
                    "invitationCode" to invitation.invitationCode,
                    "inviterUId" to invitation.inviterId,
                ),
            ).exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.uId")
            .isNotEmpty
    }

    @Test
    fun `trying to sign up with an already used username should return BAD_REQUEST`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val invitation =
            userInvitation()

        client
            .post()
            .uri(UserController.USER_BASE_URL + UserController.SIGNUP_URL)
            .bodyValue(
                mapOf(
                    "username" to INVITER_USERNAME,
                    "password" to VALID_PASSWORD,
                    "invitationCode" to invitation.invitationCode,
                    "inviterUId" to invitation.inviterId,
                ),
            ).exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `trying to sign up with an invalid password (not secure) should return BAD_REQUEST`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val invitation =
            userInvitation()

        client
            .post()
            .uri(UserController.USER_BASE_URL + UserController.SIGNUP_URL)
            .bodyValue(
                mapOf(
                    "username" to VALID_USERNAME,
                    "password" to INVALID_PASSWORD,
                    "invitationCode" to invitation.invitationCode,
                    "inviterUId" to invitation.inviterId,
                ),
            ).exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `trying to sign up with an invalid invitation code should return BAD_REQUEST`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val invitation =
            userInvitation()

        client
            .post()
            .uri(UserController.USER_BASE_URL + UserController.SIGNUP_URL)
            .bodyValue(
                mapOf(
                    "username" to VALID_USERNAME,
                    "password" to VALID_PASSWORD,
                    "invitationCode" to "invalid",
                    "inviterUId" to invitation.inviterId,
                ),
            ).exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `trying to sign up with an expired invitation code should return BAD_REQUEST`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val invitation =
            userInvitation(
                expirationDate = Timestamp.valueOf(LocalDate.now().minusDays(1).atStartOfDay()),
            )

        client
            .post()
            .uri(UserController.USER_BASE_URL + UserController.SIGNUP_URL)
            .bodyValue(
                mapOf(
                    "username" to VALID_USERNAME,
                    "password" to VALID_PASSWORD,
                    "invitationCode" to invitation.invitationCode,
                    "inviterUId" to invitation.inviterId,
                ),
            ).exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `getting a user successfully should return OK and the user username`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val uId =
            manager
                .run {
                    val user =
                        userRepo.findToken(token.token.toString()) ?: throw IllegalStateException("User not found")
                    return@run checkNotNull(user.uId) { "User not found" }
                }

        client
            .get()
            .uri(UserController.USER_BASE_URL + UserController.USER_ID_URL, uId)
            .header("Authorization", "Bearer ${token.token}")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.username")
            .isEqualTo(INVITER_USERNAME)
    }

    @Test
    fun `trying to get a user that does not exist should return NOT_FOUND`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        client
            .get()
            .uri(UserController.USER_BASE_URL + UserController.USER_ID_URL, 0)
            .header("Authorization", "Bearer ${token.token}")
            .exchange()
            .expectStatus()
            .isNotFound
    }

    @Test
    fun `trying to get a user without being authenticated should return UNAUTHORIZED`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        client
            .get()
            .uri(UserController.USER_BASE_URL + UserController.USER_ID_URL, 0)
            .exchange()
            .expectStatus()
            .isUnauthorized
    }

    @Test
    fun `logging in with valid body and token should return OK and token`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        client
            .post()
            .uri(UserController.USER_BASE_URL + UserController.LOGIN_URL)
            .bodyValue(
                mapOf(
                    "username" to INVITER_USERNAME,
                    "password" to VALID_PASSWORD,
                ),
            ).exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.token")
            .isNotEmpty
    }

    @Test
    fun `trying to log in with a non-existent username should return NOT_FOUND`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        client
            .post()
            .uri(UserController.USER_BASE_URL + UserController.LOGIN_URL)
            .bodyValue(
                mapOf(
                    "username" to NON_EXISTENT_USERNAME,
                    "password" to VALID_PASSWORD,
                ),
            ).exchange()
            .expectStatus()
            .isNotFound
    }

    @Test
    fun `trying to log in with an invalid password should return BAD_REQUEST`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        client
            .post()
            .uri(UserController.USER_BASE_URL + UserController.LOGIN_URL)
            .bodyValue(
                mapOf(
                    "username" to INVITER_USERNAME,
                    "password" to INVALID_PASSWORD,
                ),
            ).exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `creating an invitation with a valid token should return OK and the invitation`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val currentDateTime = LocalDateTime.now().plusDays(1)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val formattedDateTime = currentDateTime.format(formatter)

        client
            .post()
            .uri(UserController.USER_BASE_URL + UserController.INVITATION_URL)
            .header("Authorization", "Bearer ${token.token}")
            .bodyValue(
                mapOf(
                    "expirationDate" to formattedDateTime,
                ),
            ).exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.invitationCode")
            .isNotEmpty
    }

    @Test
    fun `trying to create an invitation without being authenticated should return UNAUTHORIZED`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        client
            .post()
            .uri(UserController.USER_BASE_URL + UserController.INVITATION_URL)
            .exchange()
            .expectStatus()
            .isUnauthorized
    }

    @Test
    fun `logging out with a valid token should return OK`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        client
            .delete()
            .uri(UserController.USER_BASE_URL + UserController.LOGOUT_URL)
            .header("Authorization", "Bearer ${token.token}")
            .exchange()
            .expectStatus()
            .isOk
    }

    @Test
    fun `trying to log out without being authenticated should return UNAUTHORIZED`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        client
            .delete()
            .uri(UserController.USER_BASE_URL + UserController.LOGOUT_URL)
            .exchange()
            .expectStatus()
            .isUnauthorized
    }

    @Test
    fun `trying to log out with an invalid token should return BAD_REQUEST`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        client
            .delete()
            .uri(UserController.USER_BASE_URL + UserController.LOGOUT_URL)
            .header("Authorization", "Bearer invalid")
            .exchange()
            .expectStatus()
            .isUnauthorized
    }
}