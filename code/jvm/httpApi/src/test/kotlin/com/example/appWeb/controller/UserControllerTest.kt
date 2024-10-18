package com.example.appWeb.controller

import TransactionManager
import com.example.appWeb.model.dto.input.user.AuthenticatedUserInputModel
import com.example.appWeb.model.dto.output.user.UserInfoOutputModel
import com.example.appWeb.model.problem.UserProblem
import jdbc.transactionManager.TransactionManagerJDBC
import mem.TransactionManagerInMem
import model.users.Password
import model.users.User
import model.users.UserToken
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.HttpStatus
import services.UserServices
import utils.encryption.DummyEncrypt
import java.util.UUID
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertIs

class UserControllerTest {
    companion object {
        @JvmStatic
        fun transactionManager(): Stream<TransactionManager> =
            Stream.of(
                TransactionManagerInMem().also { cleanup(it) },
                TransactionManagerJDBC(TestSetup.dataSource, DummyEncrypt).also { cleanup(it) },
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
    }
}