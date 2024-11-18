package com.example.appWeb.controller

import TransactionManager
import com.example.appWeb.model.dto.input.user.AuthenticatedUserInputModel
import com.example.appWeb.model.dto.input.user.UserLogInInputModel
import com.example.appWeb.model.dto.input.user.UserSignUpInputModel
import com.example.appWeb.model.dto.output.user.UserAuthenticatedOutputModel
import com.example.appWeb.model.dto.output.user.UserInfoOutputModel
import com.example.appWeb.model.dto.output.user.UserInvitationOutputModel
import com.example.appWeb.model.dto.output.user.UserSignUpOutputModel
import com.example.appWeb.model.problem.ChannelProblem
import com.example.appWeb.model.problem.UserProblem
import jdbc.transactionManager.TransactionManagerJDBC
import mem.TransactionManagerInMem
import model.channels.AccessControl
import model.channels.Channel
import model.channels.Visibility
import model.users.Password
import model.users.User
import model.users.UserInvitation
import model.users.UserToken
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.HttpStatus
import services.ChannelServices
import services.UserServices
import utils.Success
import utils.encryption.DummyEncrypt
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class UserControllerTest {
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

        private fun makeUser(
            manager: TransactionManager,
            username: String = System.currentTimeMillis().toString(),
            password: Password = Password("Password123"),
        ) = manager
            .run {
                userRepo
                    .createUser(
                        User(
                            username = username,
                            password = password,
                        ),
                    )
            }

        private fun makeInviterAndInvitation(
            manager: TransactionManager,
            username: String = "inviter",
            password: Password = Password("inviterPassword123"),
            expirationDate: Timestamp = Timestamp.valueOf(LocalDateTime.now().plusHours(1)),
        ) = manager.run {
            val inviter = userRepo.createUser(User(username = username, password = password))
            val inviterUId = checkNotNull(inviter?.uId)
            val invitation = UserInvitation(inviterUId, expirationDate)
            userRepo.createInvitation(invitation)
            return@run invitation
        }

        private fun makeToken(
            manager: TransactionManager,
            uId: UInt,
        ) = manager
            .run {
                val token = UserToken(userId = uId, token = UUID.randomUUID())
                userRepo
                    .createToken(token)
                token
            }
    }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `signing up with valid user and valid invitation code should return OK`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val userController = UserController(userServices)
        val invitation = makeInviterAndInvitation(manager)

        val userSignUpInput =
            UserSignUpInputModel(
                username = "newUser",
                password = "Password123",
                invitationCode = invitation.invitationCode.toString(),
            )

        val response = userController.signUp(userSignUpInput)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertIs<UserSignUpOutputModel>(response.body)
    }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `trying to sign up with username that already exists should return BAD_REQUEST`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val userController = UserController(userServices)
        val invitation = makeInviterAndInvitation(manager)

        val existingUser = makeUser(manager, username = "existingUser")
        checkNotNull(existingUser)
        val userSignUpInput =
            UserSignUpInputModel(
                username = existingUser.username,
                password = "Password123",
                invitationCode = invitation.invitationCode.toString(),
            )

        val response = userController.signUp(userSignUpInput)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertIs<UserProblem.UsernameAlreadyExists>(response.body)
        assertEquals(response.body, UserProblem.UsernameAlreadyExists)
    }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `trying to sign up with invalid invitation code should return BAD_REQUEST`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val userController = UserController(userServices)

        val userSignUpInput =
            UserSignUpInputModel(
                username = "newUser",
                password = "Password123",
                invitationCode = "invalidCode",
            )

        val response = userController.signUp(userSignUpInput)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertIs<UserProblem.InvitationCodeIsInvalid>(response.body)
        assertEquals(response.body, UserProblem.InvitationCodeIsInvalid)
    }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `trying to sign up with expired invitation code should return BAD_REQUEST`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val userController = UserController(userServices)
        val expiredDate = Timestamp.valueOf(LocalDateTime.now().minusHours(1))
        val expiredInvitation =
            makeInviterAndInvitation(manager, expirationDate = expiredDate)

        val userSignUpInput =
            UserSignUpInputModel(
                username = "newUser",
                password = "Password123",
                invitationCode = expiredInvitation.invitationCode.toString(),
            )

        val response = userController.signUp(userSignUpInput)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertIs<UserProblem.InvitationCodeHasExpired>(response.body)
        assertEquals(response.body, UserProblem.InvitationCodeHasExpired)
    }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `getting a user successfully should return OK and user`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val userController = UserController(userServices)

        val user = makeUser(manager)
        val userId = checkNotNull(user?.uId)
        val authenticated = AuthenticatedUserInputModel(userId, makeToken(manager, userId).token.toString())

        val userToGet = makeUser(manager)
        val userIdToGet = checkNotNull(userToGet?.uId)

        val response = userController.getUser(userIdToGet, authenticated)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertIs<UserInfoOutputModel>(response.body)
        val result = response.body as UserInfoOutputModel
        assertEquals(userToGet?.username, result.username)
    }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `trying to get a user that does not exist should return NOT_FOUND`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val userController = UserController(userServices)

        val user = makeUser(manager)
        val userId = checkNotNull(user?.uId)
        val authenticated = AuthenticatedUserInputModel(userId, makeToken(manager, userId).token.toString())

        val userIdToGet = 0u

        val response = userController.getUser(userIdToGet, authenticated)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertIs<UserProblem.UserNotFound>(response.body)
        assertEquals(response.body, UserProblem.UserNotFound)
    }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `logging in with valid user should return OK and token`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val userController = UserController(userServices)

        val user = checkNotNull(makeUser(manager, "newUser", Password("Password123")))

        val userLogInInput = UserLogInInputModel(user.username, user.password.value)

        val response = userController.login(userLogInInput)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertIs<UserAuthenticatedOutputModel>(response.body)
        assertNotNull((response.body as UserAuthenticatedOutputModel).token)
    }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `trying to log in with invalid username should return BAD_REQUEST`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val userController = UserController(userServices)

        val user = checkNotNull(makeUser(manager, "newUser"))

        val userLogInInput = UserLogInInputModel("invalidUsername", user.password.value)

        val response = userController.login(userLogInInput)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertIs<UserProblem.UserNotFound>(response.body)
        assertEquals(response.body, UserProblem.UserNotFound)
    }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `trying to login with invalid password should return BAD_REQUEST`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val userController = UserController(userServices)

        val user = checkNotNull(makeUser(manager, "newUser", Password("Password123")))

        val userLogInInput = UserLogInInputModel(user.username, "invalidPassword")

        val response = userController.login(userLogInInput)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertIs<UserProblem.PasswordIsInvalid>(response.body)
        assertEquals(response.body, UserProblem.PasswordIsInvalid)
    }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `creating an invitation successfully should return OK`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val userController = UserController(userServices)

        val user = checkNotNull(makeUser(manager))
        val userId = checkNotNull(user.uId)
        val authenticated = AuthenticatedUserInputModel(userId, makeToken(manager, userId).token.toString())

        val response = userController.createInvitation(authenticated)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertIs<UserInvitationOutputModel>(response.body)
        assertNotNull((response.body as UserInvitationOutputModel).invitationCode)
    }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `trying to create an invitation with invalid user should return NOT_FOUND`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val userController = UserController(userServices)

        val invalidUserId = 0u

        val response = userController.createInvitation(AuthenticatedUserInputModel(invalidUserId, ""))
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertIs<UserProblem.InviterNotFound>(response.body)
        assertEquals(response.body, UserProblem.InviterNotFound)
    }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `logging out successfully should return OK`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val userController = UserController(userServices)

        val user = checkNotNull(makeUser(manager))
        val userId = checkNotNull(user.uId)
        val token = makeToken(manager, userId).token.toString()
        val authenticated = AuthenticatedUserInputModel(userId, token)

        val response = userController.logout(authenticated)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNull(response.body)
    }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `trying to logout with invalid token should return BAD_REQUEST`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val userController = UserController(userServices)

        val user = checkNotNull(makeUser(manager))
        val userId = checkNotNull(user.uId)

        val response = userController.logout(AuthenticatedUserInputModel(userId, "invalidToken"))
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertIs<UserProblem.TokenNotFound>(response.body)
        assertEquals(response.body, UserProblem.TokenNotFound)
    }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `trying to logout with invalid user should return NOT_FOUND`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val userController = UserController(userServices)

        val user = checkNotNull(makeUser(manager))
        val userId = checkNotNull(user.uId)
        val token = makeToken(manager, userId).token.toString()

        val response = userController.logout(AuthenticatedUserInputModel(0u, token))
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertIs<UserProblem.UserNotFound>(response.body)
        assertEquals(response.body, UserProblem.UserNotFound)
    }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `joining a channel with valid user and valid invitation code should return OK`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val channelServices = ChannelServices(manager)
        val userController = UserController(userServices)

        val user = checkNotNull(makeUser(manager))
        val userId = checkNotNull(user.uId)
        val authenticated = AuthenticatedUserInputModel(userId, makeToken(manager, userId).token.toString())
        val owner = checkNotNull(makeUser(manager))
        val ownerId = checkNotNull(owner.uId)
        val channel =
            channelServices
                .createChannel(
                    ownerId,
                    "channel",
                    AccessControl.READ_WRITE.name,
                    Visibility.PRIVATE.name,
                ) as Success<Channel>
        val channelId = checkNotNull(channel.value.cId)
        val invitationCode =
            channelServices.createChannelInvitation(
                channelId,
                1u,
                LocalDate.now().plusDays(1).toString(),
                AccessControl.READ_WRITE.name,
                ownerId,
            ) as Success<UUID>

        val response = userController.joinChannel(channelId, invitationCode.value.toString(), authenticated)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNull(response.body)
    }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `trying to join a channel with invalid user should return NOT_FOUND`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val channelServices = ChannelServices(manager)
        val userController = UserController(userServices)

        val user = checkNotNull(makeUser(manager))
        val userId = checkNotNull(user.uId)
        val authenticated = AuthenticatedUserInputModel(0u, makeToken(manager, userId).token.toString())
        val owner = checkNotNull(makeUser(manager))
        val ownerId = checkNotNull(owner.uId)
        val channel =
            channelServices
                .createChannel(
                    ownerId,
                    "channel",
                    AccessControl.READ_WRITE.name,
                    Visibility.PUBLIC.name,
                ) as Success<Channel>
        val channelId = checkNotNull(channel.value.cId)
        val invitationCode =
            channelServices.createChannelInvitation(
                channelId,
                1u,
                LocalDate.now().plusDays(1).toString(),
                AccessControl.READ_WRITE.name,
                ownerId,
            ) as Success<UUID>

        val response = userController.joinChannel(channelId, invitationCode.value.toString(), authenticated)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertIs<UserProblem.UserNotFound>(response.body)
        assertEquals(response.body, UserProblem.UserNotFound)
    }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `trying to join a channel that does not exists should return NOT_FOUND`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val userController = UserController(userServices)

        val user = checkNotNull(makeUser(manager))
        val userId = checkNotNull(user.uId)
        val authenticated = AuthenticatedUserInputModel(userId, makeToken(manager, userId).token.toString())

        val channelId = 0u
        val invitationCode = "invitationCode"

        val response = userController.joinChannel(channelId, invitationCode, authenticated)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertIs<ChannelProblem.ChannelNotFound>(response.body)
        assertEquals(response.body, ChannelProblem.ChannelNotFound)
    }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `trying to join a channel with invalid invitation code should return BAD_REQUEST`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val channelServices = ChannelServices(manager)
        val userController = UserController(userServices)

        val user = checkNotNull(makeUser(manager))
        val userId = checkNotNull(user.uId)
        val authenticated = AuthenticatedUserInputModel(userId, makeToken(manager, userId).token.toString())
        val owner = checkNotNull(makeUser(manager))
        val ownerId = checkNotNull(owner.uId)
        val channel =
            channelServices
                .createChannel(
                    ownerId,
                    "channel",
                    AccessControl.READ_WRITE.name,
                    Visibility.PRIVATE.name,
                ) as Success<Channel>
        val channelId = checkNotNull(channel.value.cId)

        val response = userController.joinChannel(channelId, "invalidInvitationCode", authenticated)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertIs<UserProblem.InvitationCodeIsInvalid>(response.body)
        assertEquals(response.body, UserProblem.InvitationCodeIsInvalid)
    }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `trying to join a channel with expired invitation code should return BAD_REQUEST`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val channelServices = ChannelServices(manager)
        val userController = UserController(userServices)

        val user = checkNotNull(makeUser(manager))
        val userId = checkNotNull(user.uId)
        val authenticated = AuthenticatedUserInputModel(userId, makeToken(manager, userId).token.toString())
        val owner = checkNotNull(makeUser(manager))
        val ownerId = checkNotNull(owner.uId)
        val channel =
            channelServices
                .createChannel(
                    ownerId,
                    "channel",
                    AccessControl.READ_WRITE.name,
                    Visibility.PRIVATE.name,
                ) as Success<Channel>
        val channelId = checkNotNull(channel.value.cId)
        val invitationCode =
            channelServices.createChannelInvitation(
                channelId,
                1u,
                LocalDate.now().minusDays(1).toString(),
                AccessControl.READ_WRITE.name,
                ownerId,
            ) as Success<UUID>

        val response = userController.joinChannel(channelId, invitationCode.value.toString(), authenticated)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertIs<UserProblem.InvitationCodeHasExpired>(response.body)
        assertEquals(response.body, UserProblem.InvitationCodeHasExpired)
    }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `trying to join a channel with max uses reached should return BAD_REQUEST`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val channelServices = ChannelServices(manager)
        val userController = UserController(userServices)

        val user = checkNotNull(makeUser(manager))
        val userId = checkNotNull(user.uId)
        val authenticated = AuthenticatedUserInputModel(userId, makeToken(manager, userId).token.toString())
        val owner = checkNotNull(makeUser(manager))
        val ownerId = checkNotNull(owner.uId)
        val channel =
            channelServices
                .createChannel(
                    ownerId,
                    "channel",
                    AccessControl.READ_WRITE.name,
                    Visibility.PRIVATE.name,
                ) as Success<Channel>
        val channelId = checkNotNull(channel.value.cId)
        val invitationCode =
            channelServices.createChannelInvitation(
                channelId,
                0u,
                LocalDate.now().plusDays(1).toString(),
                AccessControl.READ_WRITE.name,
                ownerId,
            ) as Success<UUID>

        val response = userController.joinChannel(channelId, invitationCode.value.toString(), authenticated)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertIs<UserProblem.InvitationCodeMaxUsesReached>(response.body)
        assertEquals(response.body, UserProblem.InvitationCodeMaxUsesReached)
    }
}