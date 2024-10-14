package services

import TransactionManager
import jdbc.transactionManager.TransactionManagerJDBC
import mem.TransactionManagerInMem
import model.users.Password
import model.users.User
import model.users.UserInvitation
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import utils.Failure
import utils.Success
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.UUID
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class UserServicesTest {
	private val validPassword = "Password123"
	private val passwordDefault = Password(validPassword)
	private val usernameDefault1 = "name"
	private val usernameDefault2 = "name2"
	private val uIdDefault = 1u
	private val cIdDefault = 1u
	private val invitationCodeDefault = UUID.fromString("0f7ed58e-89c0-4331-b22d-0d075b356317")
	private val userDefault = User(null, usernameDefault1, passwordDefault)
	private val userWithIdDefault = User(uIdDefault, usernameDefault1, passwordDefault)

	companion object {
		@JvmStatic
		fun transactionManagers(): Stream<TransactionManager> =
			Stream.of(
				TransactionManagerInMem().also { cleanup(it) },
				TransactionManagerJDBC(Environment).also { cleanup(it) },
			)

		private fun cleanup(manager: TransactionManager) =
			manager.run {
				userRepo.clear()
				channelRepo.clear()
				messageRepo.clear()
			}
	}

	/**
	 * Create an inviter and an invitation in the database.
	 * @param manager The transaction manager.
	 * @return The ID of the new inviter.
	 */
	private fun makeInviter(manager: TransactionManager): UInt {
		val inviter =
			manager.run {
				userRepo.createUser(userDefault)
			}

		val inviterUId = checkNotNull(inviter?.uId)

		manager.run {
			userRepo.createInvitation(
				UserInvitation(
					inviterUId,
					Timestamp.valueOf(LocalDateTime.now().plusDays(1)),
					invitationCodeDefault,
				),
			)
		}
		return inviterUId
	}

	@ParameterizedTest
	@MethodSource("transactionManagers")
	fun `creating a new user successfully should return the User`(manager: TransactionManager) {
		val userServices = UserServices(manager)
		val inviterUId = makeInviter(manager)
		val newUser =
			userServices
				.createUser(usernameDefault2, validPassword, invitationCodeDefault.toString(), inviterUId)

		assertIs<Success<User>>(newUser, "User creation failed with error" + (newUser as? Failure)?.value)
		assertNotNull(newUser.value.uId, "User id is null")
		assertEquals(usernameDefault2, newUser.value.username, "Username is different")
		assertEquals(passwordDefault, newUser.value.password, "Password is different")
		assertIs<UUID>(newUser.value.token, "Token is not a UUID")
	}
}
