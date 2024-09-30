package services

import io.mockk.*
import ChannelRepositoryInterface
import UserRepositoryInterface
import errors.Error
import model.*
import org.eclipse.jetty.util.security.Password
import org.junit.jupiter.api.BeforeEach
import utils.Either
import kotlin.test.Test
import kotlin.test.assertEquals

class UserServicesTest {

	private lateinit var userRepo: UserRepositoryInterface
	private lateinit var channelRepo: ChannelRepositoryInterface
	private lateinit var userServices: UserServices

	private val passwordDefault = Password("password")
	private val usernameDefault = "name"
	private val uIdDefault = 1u
	private val cIdDefault = 1u
	private val userDefault = User(null, usernameDefault, passwordDefault)
	private val userWithIdDefault = User(uIdDefault, usernameDefault, passwordDefault)
	private val userInfoDefault = UserInfo(uIdDefault, usernameDefault)
	private val channelNameDefault = ChannelName("name", "name")
	private val accessControlDefault = AccessControl.READ_WRITE
	private val channelDefault = Channel.Public(cIdDefault, userInfoDefault, channelNameDefault, accessControlDefault)

	@BeforeEach
	fun setup() {
		userRepo = mockk()
		channelRepo = mockk()
		userServices = UserServices(userRepo, channelRepo)
	}

	@Test
	fun `create a new user should return the user created with uID attribute`() {
		val user = userDefault
		every { userRepo.createUser(user) } returns userWithIdDefault

		val userCreated = userServices.createUser(user) as Either.Right
		val userId = userCreated.value.uId

		assertEquals(uIdDefault, userId)

		verify { userRepo.createUser(user) }
	}

	@Test
	fun `trying to create a user that already exists should return an error`() {
		val user = userDefault
		every { userRepo.createUser(user) } returns null

		val userCreated = userServices.createUser(user) as Either.Left
		val error = userCreated.value

		assertEquals(Error.UserAlreadyExists, error)

		verify { userRepo.createUser(user) }
	}

	@Test
	fun `successfully joining a channel should return a success message`() {
		val userId = uIdDefault
		val channelId = cIdDefault
		every { userRepo.findById(userId) } returns userWithIdDefault
		every { channelRepo.findById(channelId) } returns channelDefault
		every { userRepo.joinChannel(userId, channelId) } just Runs

		val result = userServices.joinChannel(userId, channelId) as Either.Right
		val message = result.value

		assertEquals("User joined channel successfully", message)

		verify { userRepo.findById(userId) }
		verify { channelRepo.findById(channelId) }
		verify { userRepo.joinChannel(userId, channelId) }
	}

	@Test
	fun `trying to join a channel with a user that does not exist should return an error`() {
		val userId = uIdDefault
		val channelId = cIdDefault
		every { userRepo.findById(userId) } returns null

		val result = userServices.joinChannel(userId, channelId) as Either.Left
		val error = result.value

		assertEquals(Error.UserNotFound, error)

		verify { userRepo.findById(userId) }
	}

	@Test
	fun `trying to join a channel that does not exist should return an error`() {
		val userId = uIdDefault
		val channelId = cIdDefault
		every { userRepo.findById(userId) } returns userWithIdDefault
		every { channelRepo.findById(channelId) } returns null

		val result = userServices.joinChannel(userId, channelId) as Either.Left
		val error = result.value

		assertEquals(Error.ChannelNotFound, error)

		verify { userRepo.findById(userId) }
		verify { channelRepo.findById(channelId) }
	}
}