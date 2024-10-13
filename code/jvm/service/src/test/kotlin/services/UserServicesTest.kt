package services

import TransactionManager
import errors.ChannelError
import errors.ChannelError.ChannelNotFound
import errors.UserError
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.Password
import model.User
import org.junit.jupiter.api.BeforeEach
import utils.Either
import utils.failure
import utils.success
import kotlin.test.Test
import kotlin.test.assertEquals

class UserServicesTest {
	private lateinit var tm: TransactionManager
	private lateinit var userServices: UserServices

	private val validPassword = "Password123"
	private val passwordDefault = Password(validPassword)
	private val usernameDefault = "name"
	private val uIdDefault = 1u
	private val cIdDefault = 1u
	private val invitationCodeDefault = "code"
	private val userDefault = User(null, usernameDefault, passwordDefault)
	private val userWithIdDefault = User(uIdDefault, usernameDefault, passwordDefault)

	@BeforeEach
	fun setup() {
		tm = mockk()
		userServices = UserServices(tm)
	}

	@Test
	fun `create a new user should return the user created with uID attribute`() {
		every { tm.run<Either<UserError, User>>(any()) } returns success(userWithIdDefault)

		val userCreated =
			userServices.createUser(usernameDefault, validPassword, invitationCodeDefault, uIdDefault) as Either.Right
		val userId = userCreated.value.uId

		assertEquals(uIdDefault, userId)

		verify { tm.run<Either<UserError, User>>(any()) }
	}

	@Test
	fun `trying to create a user that already exists should return an error`() {
		every { tm.run<Either<UserError, User>>(any()) } returns failure(UserError.UserAlreadyExists)

		val userCreated =
			userServices.createUser(usernameDefault, validPassword, invitationCodeDefault, uIdDefault) as Either.Left
		val error = userCreated.value

		assertEquals(UserError.UserAlreadyExists, error)

		verify { tm.run<Either<UserError, User>>(any()) }
	}

	@Test
	fun `successfully joining a channel should return a success message`() {
		every { tm.run<Either<UserError, Unit>>(any()) } returns success(Unit)

		val result = userServices.joinChannel(uIdDefault, cIdDefault, invitationCodeDefault) as Either.Right
		val message = result.value

		assertEquals(Unit, message)

		verify { tm.run<Either<UserError, Unit>>(any()) }
	}

	@Test
	fun `trying to join a channel with a user that does not exist should return an error`() {
		every { tm.run<Either<UserError, Unit>>(any()) } returns failure(UserError.UserNotFound)

		val result = userServices.joinChannel(uIdDefault, cIdDefault, invitationCodeDefault) as Either.Left
		val error = result.value

		assertEquals(UserError.UserNotFound, error)

		verify { tm.run<Either<UserError, Unit>>(any()) }
	}

	@Test
	fun `trying to join a channel that does not exist should return an error`() {
		every { tm.run<Either<ChannelError, Unit>>(any()) } returns failure(ChannelNotFound)

		val result = userServices.joinChannel(uIdDefault, cIdDefault, invitationCodeDefault) as Either.Left
		val error = result.value

		assertEquals(ChannelNotFound, error)

		verify { tm.run<Either<ChannelError, Unit>>(any()) }
	}
}
