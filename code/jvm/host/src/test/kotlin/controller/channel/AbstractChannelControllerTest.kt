package controller.channel

import TransactionManager
import com.example.appWeb.controller.ChannelController
import controller.TestConfig
import model.channels.AccessControl.READ_WRITE
import model.channels.Channel
import model.channels.ChannelName
import model.channels.Visibility.PUBLIC
import model.users.Password
import model.users.User
import model.users.UserInfo
import model.users.UserToken
import org.example.appWeb.HttpApiApplication
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.test.assertEquals

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

    private fun makeChannelName(): () -> String {
        var count = 0
        return {
            count++
            "channel$count"
        }
    }

    private val channelNameMaker = makeChannelName()

    private val channelName: String
        get() {
            return channelNameMaker()
        }

    private lateinit var token: UserToken

    private fun encodeName(name: String): String = URLEncoder.encode(name, StandardCharsets.UTF_8.toString())

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
    fun `create a channel`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        client
            .post()
            .uri(ChannelController.CHANNEL_BASE_URL)
            .header("Authorization", "Bearer ${token.token}")
            .bodyValue(
                mapOf(
                    "name" to channelName,
                    "accessControl" to READ_WRITE.name,
                    "visibility" to PUBLIC.name,
                ),
            ).exchange()
            .expectStatus()
            .isOk
    }

    @Test
    fun `fail to create a channel due invalid token`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        client
            .post()
            .uri(ChannelController.CHANNEL_BASE_URL)
            .header("Authorization", "Bearer invalid")
            .bodyValue(
                mapOf(
                    "name" to channelName,
                    "accessControl" to READ_WRITE.name,
                    "visibility" to PUBLIC.name,
                ),
            ).exchange()
            .expectStatus()
            .isUnauthorized
    }

    @Test
    fun `fail to create a channel due missing token`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        client
            .post()
            .uri(ChannelController.CHANNEL_BASE_URL)
            .bodyValue(
                mapOf(
                    "name" to channelName,
                    "accessControl" to READ_WRITE.name,
                    "visibility" to PUBLIC.name,
                ),
            ).exchange()
            .expectStatus()
            .isUnauthorized
    }

    @Test
    fun `create channel invitation`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val channel =
            manager.run {
                val user =
                    userRepo
                        .findByToken(token.token.toString())
                        ?: throw IllegalStateException("User not found")
                channelRepo
                    .createChannel(
                        Channel.createChannel(
                            owner = UserInfo(token.uId, user.username),
                            name = ChannelName(channelName, user.username),
                            visibility = PUBLIC,
                            accessControl = READ_WRITE,
                        ),
                    )
                    ?: throw IllegalStateException("Channel not created")
            }

        val channelId = checkNotNull(channel.cId) { "Channel not created" }

        client
            .post()
            .uri("${ChannelController.CHANNEL_BASE_URL}${ChannelController.CHANNEL_INVITATION_URL}")
            .header("Authorization", "Bearer ${token.token}")
            .bodyValue(
                mapOf(
                    "channelId" to channelId,
                    "maxUses" to 1,
                    "accessControl" to READ_WRITE.name,
                ),
            ).exchange()
            .expectStatus()
            .isOk
    }

    @Test
    fun `fail to create channel invitation due invalid channelId`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        client
            .post()
            .uri("${ChannelController.CHANNEL_BASE_URL}${ChannelController.CHANNEL_INVITATION_URL}")
            .header("Authorization", "Bearer ${token.token}")
            .bodyValue(
                mapOf(
                    "channelId" to 0,
                    "maxUses" to 1,
                    "accessControl" to READ_WRITE.name,
                ),
            ).exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `fail to create channel invitation due invalid token`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        client
            .post()
            .uri("${ChannelController.CHANNEL_BASE_URL}${ChannelController.CHANNEL_INVITATION_URL}")
            .header("Authorization", "Bearer invalid")
            .bodyValue(
                mapOf(
                    "channelId" to 0,
                    "maxUses" to 1,
                    "accessControl" to READ_WRITE.name,
                ),
            ).exchange()
            .expectStatus()
            .isUnauthorized
    }

    @Test
    fun `fail to create channel invitation due missing token`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        client
            .post()
            .uri("${ChannelController.CHANNEL_BASE_URL}${ChannelController.CHANNEL_INVITATION_URL}")
            .bodyValue(
                mapOf(
                    "channelId" to 0,
                    "maxUses" to 1,
                    "accessControl" to READ_WRITE.name,
                ),
            ).exchange()
            .expectStatus()
            .isUnauthorized
    }

    @Test
    fun `fail to create channel invitation due invalid access control`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        client
            .post()
            .uri("${ChannelController.CHANNEL_BASE_URL}${ChannelController.CHANNEL_INVITATION_URL}")
            .header("Authorization", "Bearer ${token.token}")
            .bodyValue(
                mapOf(
                    "channelId" to 0,
                    "maxUses" to 1,
                    "accessControl" to "invalid",
                ),
            ).exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `fail to create channel invitation due invalid max uses`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        client
            .post()
            .uri("${ChannelController.CHANNEL_BASE_URL}${ChannelController.CHANNEL_INVITATION_URL}")
            .header("Authorization", "Bearer ${token.token}")
            .bodyValue(
                mapOf(
                    "channelId" to 0,
                    "maxUses" to 0,
                    "accessControl" to READ_WRITE.name,
                ),
            ).exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `create channel invitation with default accessControl`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val channel =
            manager.run {
                val user =
                    userRepo
                        .findToken(token.token.toString())
                        ?.let { userRepo.findById(it.uId) }
                        ?: throw IllegalStateException("User not found")
                channelRepo
                    .createChannel(
                        Channel.createChannel(
                            owner = UserInfo(token.uId, user.username),
                            name = ChannelName(channelName, user.username),
                            visibility = PUBLIC,
                            accessControl = READ_WRITE,
                        ),
                    )
                    ?: throw IllegalStateException("Channel not created")
            }

        val channelId = checkNotNull(channel.cId) { "Channel not created" }

        client
            .post()
            .uri("${ChannelController.CHANNEL_BASE_URL}${ChannelController.CHANNEL_INVITATION_URL}")
            .header("Authorization", "Bearer ${token.token}")
            .bodyValue(
                mapOf(
                    "channelId" to channelId,
                    "maxUses" to 1,
                ),
            ).exchange()
            .expectStatus()
            .isOk

        val invitation =
            manager.run {
                channelRepo.findInvitation(channelId) ?: throw IllegalStateException("Invitation not created")
            }
        assertEquals(channel.accessControl, invitation.accessControl)
    }

    @Test
    fun `get channels`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        client
            .get()
            .uri(ChannelController.CHANNEL_BASE_URL)
            .header("Authorization", "Bearer ${token.token}")
            .exchange()
            .expectStatus()
            .isOk
    }

    @Test
    fun `get channel`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val channel =
            manager.run {
                val user =
                    userRepo
                        .findToken(token.token.toString())
                        ?.let { userRepo.findById(it.uId) }
                        ?: throw IllegalStateException("User not found")
                channelRepo
                    .createChannel(
                        Channel.createChannel(
                            owner = UserInfo(token.uId, user.username),
                            name = ChannelName(channelName, user.username),
                            visibility = PUBLIC,
                            accessControl = READ_WRITE,
                        ),
                    )
                    ?: throw IllegalStateException("Channel not created")
            }

        val channelId = checkNotNull(channel.cId) { "Channel not created" }

        client
            .get()
            .uri("${ChannelController.CHANNEL_BASE_URL}/$channelId")
            .header("Authorization", "Bearer ${token.token}")
            .exchange()
            .expectStatus()
            .isOk
    }

    @Test
    fun `get channel by name`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val channel =
            manager.run {
                val user =
                    userRepo
                        .findToken(token.token.toString())
                        ?.let { userRepo.findById(it.uId) }
                        ?: throw IllegalStateException("User not found")
                channelRepo
                    .createChannel(
                        Channel.createChannel(
                            owner = UserInfo(token.uId, user.username),
                            name = ChannelName(channelName, user.username),
                            visibility = PUBLIC,
                            accessControl = READ_WRITE,
                        ),
                    )
                    ?: throw IllegalStateException("Channel not created")
            }

        val channelName = checkNotNull(channel.name) { "Channel not created" }

        client
            .get()
            .uri("${ChannelController.CHANNEL_BASE_URL}/name/${encodeName(channelName.fullName)}")
            .header("Authorization", "Bearer ${token.token}")
            .exchange()
            .expectStatus()
            .isOk
    }

    @Test
    fun `get channel list by name`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val channel =
            manager.run {
                val user =
                    userRepo
                        .findToken(token.token.toString())
                        ?.let { userRepo.findById(it.uId) }
                        ?: throw IllegalStateException("User not found")
                channelRepo
                    .createChannel(
                        Channel.createChannel(
                            owner = UserInfo(token.uId, user.username),
                            name = ChannelName(channelName, user.username),
                            visibility = PUBLIC,
                            accessControl = READ_WRITE,
                        ),
                    )
                    ?: throw IllegalStateException("Channel not created")
            }

        val channelName = checkNotNull(channel.name) { "Channel not created" }

        client
            .get()
            .uri("${ChannelController.CHANNEL_BASE_URL}/search/${encodeName(channelName.fullName)}")
            .header("Authorization", "Bearer ${token.token}")
            .exchange()
            .expectStatus()
            .isOk
    }
}