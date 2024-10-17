package jdbc

import model.users.Password
import model.users.User
import model.users.UserInvitation
import model.users.UserToken
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.postgresql.ds.PGSimpleDataSource
import utils.encryption.DummyEncrypt
import java.sql.Connection
import java.sql.SQLException
import java.sql.Timestamp
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class UserJDBCTest {
    private val validPassword = "Password123"
    private val passwordDefault = Password(validPassword)

    companion object {
        private fun runWithConnection(block: (Connection) -> Unit) =
            PGSimpleDataSource()
                .apply { setURL(Environment.getDbUrl()) }
                .connection
                .let(block)
    }

    @BeforeEach
    fun clean() {
        runWithConnection { connection ->
            ChannelJDBC(connection, DummyEncrypt).clear()
            UserJDBC(connection).clear()
            MessageJDBC(connection).clear()
            UserJDBC(connection).clear()
        }
    }

    private fun createUser(
        connection: Connection,
        username: String = "user${System.currentTimeMillis()}",
        password: Password = passwordDefault,
    ) = UserJDBC(connection)
        .createUser(
            User(
                username = username,
                password = password,
            ),
        ).let { user ->
            checkNotNull(user)
            user
        }

    @Test
    fun `creating a user should successfully return the user with id`() {
        runWithConnection { connection ->
            val username = "user${System.currentTimeMillis()}"
            val user = createUser(connection, username, passwordDefault)
            assertEquals(username, user.username)
            assertEquals(passwordDefault, user.password)
            assertNotNull(user.uId)
        }
    }

    @Test
    fun `trying to create 2 users with the same username should return an SQLException`() {
        runWithConnection { connection ->
            val username = "user${System.currentTimeMillis()}"
            createUser(connection, username, passwordDefault)
            assertThrows<SQLException> {
                createUser(connection, username, passwordDefault)
            }
        }
    }

    @Test
    fun `finding a user by id should return the user`() {
        runWithConnection { connection ->
            val user = createUser(connection)
            val userId = checkNotNull(user.uId)
            val foundUser = UserJDBC(connection).findById(userId)
            assertEquals(user, foundUser)
        }
    }

    @Test
    fun `trying to find a user by id that does not exist should return null`() {
        runWithConnection { connection ->
            val foundUser = UserJDBC(connection).findById(0u)
            assertEquals(null, foundUser)
        }
    }

    @Test
    fun `finding all users with an offset and limit should return the correct users`() {
        runWithConnection { connection ->
            val users =
                (1..10).map {
                    createUser(connection, "user$it")
                }
            val foundUsers = UserJDBC(connection).findAll(5, 5)
            assertEquals(users.drop(5), foundUsers)
        }
    }

    @Test
    fun `updating a user should successfully update the user in DB`() {
        runWithConnection { connection ->
            val user = createUser(connection)
            val newUser = user.copy(username = "newUsername")
            UserJDBC(connection).save(newUser)
            val foundUser = UserJDBC(connection).findById(checkNotNull(user.uId))
            assertEquals(newUser, foundUser)
        }
    }

    @Test
    fun `deleting a user should successfully delete user from DB`() {
        runWithConnection { connection ->
            val user = createUser(connection)
            UserJDBC(connection).deleteById(checkNotNull(user.uId))
            val foundUser = UserJDBC(connection).findById(checkNotNull(user.uId))
            assertEquals(null, foundUser)
        }
    }

    @Test
    fun `finding a user by username should return the correct user`() {
        runWithConnection { connection ->
            val user = createUser(connection)
            val foundUser = UserJDBC(connection).findByUsername(user.username)
            assertEquals(user, foundUser)
        }
    }

    @Test
    fun `trying to find a user by username that does not exist should return null`() {
        runWithConnection { connection ->
            val foundUser = UserJDBC(connection).findByUsername("nonExistentUsername")
            assertEquals(null, foundUser)
        }
    }

    @Test
    fun `finding a user by token should return the correct user`() {
        runWithConnection { connection ->
            val user = createUser(connection)
            val userId = checkNotNull(user.uId)
            val token = UserToken(userId)
            UserJDBC(connection).createToken(token)
            val foundUser = UserJDBC(connection).findByToken(token.token.toString())
            assertEquals(user, foundUser)
        }
    }

    @Test
    fun `trying to find a user by token that does not exist should return null`() {
        runWithConnection { connection ->
            val foundUser = UserJDBC(connection).findByToken("nonExistentToken")
            assertEquals(null, foundUser)
        }
    }

    @Test
    fun `creating a token successfully should return true`() {
        runWithConnection { connection ->
            val user = createUser(connection)
            val userId = checkNotNull(user.uId)
            val token = UserToken(userId)
            val result = UserJDBC(connection).createToken(token)
            assertEquals(true, result)
        }
    }

    @Test
    fun `after creating max number of tokens for a user, creating a new one should delete the oldest token`() {
        runWithConnection { connection ->
            val user = createUser(connection)
            val userId = checkNotNull(user.uId)
            val tokens =
                (1..5).map {
                    UserToken(
                        userId = userId,
                        creationDate = Timestamp.valueOf(LocalDateTime.now().plusHours(it.toLong())),
                        expirationDate = Timestamp.valueOf(LocalDateTime.now().plusHours(it.toLong() + 1)),
                    )
                }
            tokens.forEach {
                UserJDBC(connection).createToken(it)
            }
            val newToken =
                UserToken(
                    userId = userId,
                    creationDate = Timestamp.valueOf(LocalDateTime.now().plusDays(1)),
                    expirationDate = Timestamp.valueOf(LocalDateTime.now().plusDays(2)),
                )
            val oldestToken = tokens.minByOrNull { it.creationDate }
            UserJDBC(connection).createToken(newToken)
            checkNotNull(oldestToken)
            assertFalse(UserJDBC(connection).validateToken(oldestToken.token.toString()))
            assertTrue(UserJDBC(connection).validateToken(newToken.token.toString()))
        }
    }

    @Test
    fun `creating an invitation successfully should create the invitation in DB`() {
        runWithConnection { connection ->
            val user = createUser(connection)
            val userId = checkNotNull(user.uId)
            val userInvitation = UserInvitation(userId, Timestamp.valueOf(LocalDateTime.now().plusDays(1)))
            UserJDBC(connection).createInvitation(userInvitation)
            val result = UserJDBC(connection).findInvitation(userId, userInvitation.invitationCode.toString())
            val resultID = checkNotNull(result?.inviterId)
            assertEquals(userInvitation.inviterId, resultID)
        }
    }

    @Test
    fun `deleting an invitation successfully should delete the invitation from DB`() {
        runWithConnection { connection ->
            val user = createUser(connection)
            val userId = checkNotNull(user.uId)
            val userInvitation = UserInvitation(userId, Timestamp.valueOf(LocalDateTime.now().plusDays(1)))
            UserJDBC(connection).createInvitation(userInvitation)
            UserJDBC(connection).deleteInvitation(userInvitation)
            val result = UserJDBC(connection).findInvitation(userId, userInvitation.invitationCode.toString())
            assertEquals(null, result)
        }
    }

    @Test
    fun `finding an existing invitation should return the correct invitation`() {
        runWithConnection { connection ->
            val user = createUser(connection)
            val userId = checkNotNull(user.uId)
            val userInvitation = UserInvitation(userId, Timestamp.valueOf(LocalDateTime.now().plusDays(1)))
            UserJDBC(connection).createInvitation(userInvitation)
            val result = UserJDBC(connection).findInvitation(userId, userInvitation.invitationCode.toString())
            assertEquals(userInvitation.inviterId, result?.inviterId)
        }
    }

    @Test
    fun `validating a valid token should return true`() {
        runWithConnection { connection ->
            val user = createUser(connection)
            val userId = checkNotNull(user.uId)
            val token = UserToken(userId)
            UserJDBC(connection).createToken(token)
            val result = UserJDBC(connection).validateToken(token.token.toString())
            assertEquals(true, result)
        }
    }

    @Test
    fun `validating an invalid token should return false`() {
        runWithConnection { connection ->
            val result = UserJDBC(connection).validateToken("invalid")
            assertEquals(false, result)
        }
    }

    @Test
    fun `deleting a token successfully should delete the token from DB`() {
        runWithConnection { connection ->
            val user = createUser(connection)
            val userId = checkNotNull(user.uId)
            val token = UserToken(userId)
            UserJDBC(connection).createToken(token)
            UserJDBC(connection).deleteToken(token.token.toString())
            val result = UserJDBC(connection).validateToken(token.token.toString())
            assertEquals(false, result)
        }
    }

    @Test
    fun `trying to delete a non-existent token should return false`() {
        runWithConnection { connection ->
            val result = UserJDBC(connection).deleteToken("nonExistentToken")
            assertEquals(false, result)
        }
    }
}