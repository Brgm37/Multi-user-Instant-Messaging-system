package com.example.appWeb.controller

import TransactionManager
import com.example.appWeb.model.dto.input.channel.CreateChannelInputModel
import com.example.appWeb.model.dto.input.channel.CreateChannelInvitationInputModel
import com.example.appWeb.model.dto.input.channel.JoinChannelInputModel
import com.example.appWeb.model.dto.input.user.AuthenticatedUserInputModel
import com.example.appWeb.model.dto.output.channel.ChannelInvitationOutputModel
import com.example.appWeb.model.dto.output.channel.ChannelListOutputModel
import com.example.appWeb.model.dto.output.channel.ChannelOutputModel
import com.example.appWeb.model.problem.ChannelProblem
import com.example.appWeb.model.problem.UserProblem
import jdbc.transactionManager.TransactionManagerJDBC
import mem.TransactionManagerInMem
import model.channels.AccessControl.READ_WRITE
import model.channels.Channel
import model.channels.ChannelInvitation
import model.channels.Visibility.PRIVATE
import model.channels.Visibility.PUBLIC
import model.users.Password
import model.users.User
import model.users.UserToken
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.HttpStatus
import services.ChannelServices
import utils.Success
import utils.encryption.DummyEncrypt
import java.util.UUID
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ChannelControllerTest {
    companion object {
        @JvmStatic
        fun transactionManager(): Stream<TransactionManager> =
            Stream.of(
                TransactionManagerInMem().also(::cleanup),
                TransactionManagerJDBC(TestSetup.dataSource, DummyEncrypt).also(::cleanup),
            )

        private fun cleanup(manager: TransactionManager) {
            manager.run {
                channelRepo.clear()
                messageRepo.clear()
                userRepo.clear()
            }
        }

        private var nr = 0

        private fun makeUser(manager: TransactionManager) =
            manager
                .run {
                    userRepo
                        .createUser(
                            User(
                                username = "owner${nr++}",
                                password = Password("Password123"),
                            ),
                        )
                }

        private fun makeToken(
            manager: TransactionManager,
            uId: UInt,
        ) = manager
            .run {
                val token = UserToken(uId = uId, token = UUID.randomUUID())
                userRepo
                    .createToken(token)
                token
            }

        private fun testSetUp(
            manager: TransactionManager,
            block: ChannelController.(AuthenticatedUserInputModel, ChannelServices) -> Unit,
        ) {
            val owner = makeUser(manager)
            val ownerId = checkNotNull(owner?.uId) { "Owner id is null" }
            val channelServices = ChannelServices(manager)
            val channelController = ChannelController(channelServices)
            val u = manager.run { userRepo.findById(ownerId) }
            checkNotNull(u) { "User not found" }
            val authenticated = AuthenticatedUserInputModel(ownerId, makeToken(manager, ownerId).token.toString())
            channelController.block(authenticated, channelServices)
        }
    }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `get a channel`(manager: TransactionManager) =
        testSetUp(manager) { authenticated, channelServices ->
            val newChannel =
                channelServices
                    .createChannel(authenticated.uId, "name", READ_WRITE.name, PUBLIC.name)
            assertIs<Success<Channel>>(newChannel, "Channel creation failed")
            val cId = checkNotNull(newChannel.value.cId) { "Channel id is null" }
            getChannel(cId, authenticated).let { resp ->
                assertEquals(HttpStatus.OK, resp.statusCode, "Status code is different")
                assertIs<ChannelOutputModel>(resp.body, "Body is not a ChannelOutputModel")
                val outputModel = resp.body as ChannelOutputModel
                assertEquals(cId, outputModel.id, "Channel id is different")
                assertEquals(authenticated.uId, outputModel.owner.id, "Owner id is different")
                assertTrue { outputModel.name.name.startsWith("@owner") }
                assertEquals(READ_WRITE.name, outputModel.accessControl, "Access control is different")
                assertEquals(PUBLIC.name, outputModel.visibility, "Visibility is different")
            }
        }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `fail to get a channel`(manager: TransactionManager) =
        testSetUp(manager) { authenticated, _ ->
            getChannel(1u, authenticated).let { resp ->
                assertEquals(HttpStatus.NOT_FOUND, resp.statusCode, "Status code is different")
                assertIs<ChannelProblem.ChannelNotFound>(resp.body, "Body is not a ChannelProblem.ChannelNotFound")
            }
        }

    @ParameterizedTest
    @MethodSource("transactionManager")
    @Suppress("UNCHECKED_CAST")
    fun `get channels`(manager: TransactionManager) =
        testSetUp(manager) { authenticated, channelServices ->
            val nr = 10
            repeat(nr) {
                channelServices.createChannel(authenticated.uId, "name$it", READ_WRITE.name, PUBLIC.name)
            }
            getChannels(authenticated).let { resp ->
                assertEquals(HttpStatus.OK, resp.statusCode, "Status code is different")
                assertIs<List<ChannelListOutputModel>>(resp.body, "Body is not a List<ChannelOutputModel>")
                val outputModels = resp.body as List<ChannelListOutputModel>
                assertEquals(nr, outputModels.size, "Number of channels is different")
                val outputModel = outputModels.first()
                assertEquals(authenticated.uId, outputModel.owner.id, "Owner id is different")
            }
        }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `create channel`(manager: TransactionManager) =
        testSetUp(manager) { authenticated, _ ->
            createChannel(
                CreateChannelInputModel(
                    name = "name",
                    accessControl = READ_WRITE.name,
                    visibility = PUBLIC.name,
                    description = null,
                    icon = null,
                ),
                authenticated,
            ).let { resp ->
                assertEquals(HttpStatus.OK, resp.statusCode, "Status code is different")
                assertIs<ChannelOutputModel>(resp.body, "Body is not a ChannelOutputModel")
                val outputModel = resp.body as ChannelOutputModel
                assertEquals(authenticated.uId, outputModel.owner.id, "Owner id is different")
                assertTrue { outputModel.name.name.startsWith("@owner") }
                assertEquals(READ_WRITE.name, outputModel.accessControl, "Access control is different")
                assertEquals(PUBLIC.name, outputModel.visibility, "Visibility is different")
            }
        }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `fail to create a channel due invalid info`(manager: TransactionManager) =
        testSetUp(manager) { authenticated, _ ->
            listOf(
                CreateChannelInputModel(
                    name = "name",
                    accessControl = " ",
                    visibility = PUBLIC.name,
                    description = null,
                    icon = null,
                ),
                CreateChannelInputModel(
                    name = "name",
                    accessControl = READ_WRITE.name,
                    visibility = " ",
                    description = null,
                    icon = null,
                ),
                CreateChannelInputModel(
                    name = " ",
                    accessControl = READ_WRITE.name,
                    visibility = PUBLIC.name,
                    description = null,
                    icon = null,
                ),
            ).forEach {
                createChannel(it, authenticated)
                    .let { resp ->
                        assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode, "Status code is different")
                        assertIs<ChannelProblem.InvalidChannelInfo>(
                            resp.body,
                            "Body is not a ChannelProblem.InvalidChannelInfo",
                        )
                    }
            }
        }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `fail to create channel due invalid visibility`(manager: TransactionManager) =
        testSetUp(manager) { authenticated, _ ->
            createChannel(
                CreateChannelInputModel(
                    name = "name",
                    accessControl = READ_WRITE.name,
                    visibility = "invalid",
                    description = null,
                    icon = null,
                ),
                authenticated,
            ).let { resp ->
                assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode, "Status code is different")
                assertIs<ChannelProblem.InvalidChannelVisibility>(
                    resp.body,
                    "Body is not a ChannelProblem.InvalidChannelVisibility",
                )
            }
        }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `fail to create channel due invalid access control`(manager: TransactionManager) =
        testSetUp(manager) { authenticated, _ ->
            createChannel(
                CreateChannelInputModel(
                    name = "name",
                    accessControl = "invalid",
                    visibility = PUBLIC.name,
                    description = null,
                    icon = null,
                ),
                authenticated,
            ).let { resp ->
                assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode, "Status code is different")
                assertIs<ChannelProblem.InvalidChannelAccessControl>(
                    resp.body,
                    "Body is not a ChannelProblem.InvalidChannelAccessControl",
                )
            }
        }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `fail to create channel due user not found`(manager: TransactionManager) =
        testSetUp(manager) { _, _ ->
            createChannel(
                CreateChannelInputModel(
                    name = "name",
                    accessControl = READ_WRITE.name,
                    visibility = PUBLIC.name,
                    description = null,
                    icon = null,
                ),
                AuthenticatedUserInputModel(0u, "token"),
            ).let { resp ->
                assertEquals(HttpStatus.NOT_FOUND, resp.statusCode, "Status code is different")
                assertIs<UserProblem.UserNotFound>(resp.body, "Body is not a ChannelProblem.UserNotFound")
            }
        }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `create channel invitation`(manager: TransactionManager) =
        testSetUp(manager) { authenticated, channelServices ->
            val newChannel =
                channelServices
                    .createChannel(authenticated.uId, "name", READ_WRITE.name, PUBLIC.name)
            assertIs<Success<Channel>>(newChannel, "Channel creation failed")
            val cId = checkNotNull(newChannel.value.cId) { "Channel id is null" }
            createChannelInvitation(
                CreateChannelInvitationInputModel(
                    channelId = cId,
                    maxUses = 1u,
                    expirationDate = null,
                    accessControl = READ_WRITE.name,
                ),
                authenticated,
            ).let { resp ->
                assertEquals(HttpStatus.OK, resp.statusCode, "Status code is different")
                assertIs<ChannelInvitationOutputModel>(resp.body, "Body is not a UUID")
            }
        }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `fail to create a invitation due user not found`(manager: TransactionManager) =
        testSetUp(manager) { authenticated, channelServices ->
            val newChannel =
                channelServices
                    .createChannel(authenticated.uId, "name", READ_WRITE.name, PUBLIC.name)
            assertIs<Success<Channel>>(newChannel, "Channel creation failed")
            val cId = checkNotNull(newChannel.value.cId) { "Channel id is null" }
            createChannelInvitation(
                CreateChannelInvitationInputModel(
                    channelId = cId,
                    maxUses = 1u,
                    expirationDate = null,
                    accessControl = READ_WRITE.name,
                ),
                AuthenticatedUserInputModel(0u, "token"),
            ).let { resp ->
                assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode, "Status code is different")
                assertIs<ChannelProblem.InvalidChannelInfo>(resp.body, "Body is not a UserProblem.UserNotFound")
            }
        }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `fail to create a invitation due channel not found`(manager: TransactionManager) =
        testSetUp(manager) { authenticated, _ ->
            createChannelInvitation(
                CreateChannelInvitationInputModel(
                    channelId = 1u,
                    maxUses = 1u,
                    expirationDate = null,
                    accessControl = READ_WRITE.name,
                ),
                authenticated,
            ).let { resp ->
                assertEquals(HttpStatus.NOT_FOUND, resp.statusCode, "Status code is different")
                assertIs<ChannelProblem.ChannelNotFound>(resp.body, "Body is not a ChannelProblem.ChannelNotFound")
            }
        }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `get a channel by name`(manager: TransactionManager) =
        testSetUp(manager) { authenticated, channelServices ->
            val name = "channel1"
            val newChannel =
                channelServices
                    .createChannel(authenticated.uId, name, READ_WRITE.name, PUBLIC.name)
            assertIs<Success<Channel>>(newChannel, "Channel creation failed")
            assertEquals(
                HttpStatus.OK,
                getChannelByName(newChannel.value.name.fullName, authenticated).statusCode,
                "Status code is different",
            )
        }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `fail to get a channel by name`(manager: TransactionManager) =
        testSetUp(manager) { authenticated, _ ->
            assertEquals(HttpStatus.OK, getChannelByName("name", authenticated).statusCode, "Status code is different")
        }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `trying to join a channel with invalid user should return BAD_REQUEST`(manager: TransactionManager) =
        testSetUp(manager) { authenticatedUserInputModel, channelServices ->
            val newChannel =
                channelServices
                    .createChannel(authenticatedUserInputModel.uId, "name", READ_WRITE.name, PUBLIC.name)
            assertIs<Success<Channel>>(newChannel, "Channel creation failed")
            val cId = checkNotNull(newChannel.value.cId) { "Channel id is null" }
            val authenticated = AuthenticatedUserInputModel(123u, "")
            val join = JoinChannelInputModel(cId)
            joinChannel(join, authenticated).let { resp ->
                assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode, "Status code is different")
                assertIs<ChannelProblem.UnableToJoinChannel>(resp.body, "Body is not a ChannelProblem.ChannelNotFound")
            }
        }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `joining a channel with valid user and valid invitation code should return OK`(manager: TransactionManager) =
        testSetUp(manager) { authenticatedUserInputModel, channelServices ->
            val newChannel =
                channelServices
                    .createChannel(authenticatedUserInputModel.uId, "name", READ_WRITE.name, PUBLIC.name)
            assertIs<Success<Channel>>(newChannel, "Channel creation failed")
            val cId = checkNotNull(newChannel.value.cId) { "Channel id is null" }
            val invitation =
                channelServices.createChannelInvitation(
                    cId,
                    1u,
                    null,
                    READ_WRITE.name,
                    authenticatedUserInputModel.uId,
                )
            assertIs<Success<ChannelInvitation>>(invitation, "Channel invitation creation failed")
            val user = makeUser(manager)
            val token = makeToken(manager, checkNotNull(user?.uId) { "User id is null" })
            val userId = checkNotNull(user?.uId) { "User id is null" }
            val authenticated = AuthenticatedUserInputModel(userId, token.token.toString())
            val join = JoinChannelInputModel(cId, invitation.value.invitationCode.toString())
            assertEquals(HttpStatus.OK, joinChannel(join, authenticated).statusCode, "Status code is different")
        }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `trying to join a channel that does not exists should return BAD_REQUEST`(manager: TransactionManager) =
        testSetUp(manager) { authenticatedUserInputModel, _ ->
            val join = JoinChannelInputModel(1u)
            joinChannel(join, authenticatedUserInputModel).let { resp ->
                assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode, "Status code is different")
                assertIs<ChannelProblem.UnableToJoinChannel>(resp.body, "Body is not a ChannelProblem.ChannelNotFound")
            }
        }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `trying to join a channel with invalid invitation code should return BAD_REQUEST`(manager: TransactionManager) =
        testSetUp(manager) { authenticatedUserInputModel, channelServices ->
            val newChannel =
                channelServices
                    .createChannel(authenticatedUserInputModel.uId, "name", READ_WRITE.name, PRIVATE.name)
            assertIs<Success<Channel>>(newChannel, "Channel creation failed")
            val cId = checkNotNull(newChannel.value.cId) { "Channel id is null" }
            val invitation =
                channelServices.createChannelInvitation(
                    cId,
                    1u,
                    null,
                    READ_WRITE.name,
                    authenticatedUserInputModel.uId,
                )
            assertIs<Success<UUID>>(invitation, "Channel invitation creation failed")
            val user = makeUser(manager)
            val token = makeToken(manager, checkNotNull(user?.uId) { "User id is null" })
            val userId = checkNotNull(user?.uId) { "User id is null" }
            val authenticated = AuthenticatedUserInputModel(userId, token.token.toString())
            val join = JoinChannelInputModel(cId, "invalid")
            joinChannel(join, authenticated).let { resp ->
                assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode, "Status code is different")
                assertIs<ChannelProblem.UnableToJoinChannel>(
                    resp.body,
                    "Body is not a ChannelProblem.UnableToJoinChannel",
                )
            }
        }
}