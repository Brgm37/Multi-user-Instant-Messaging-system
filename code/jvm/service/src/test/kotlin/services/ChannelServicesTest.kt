package services

import errors.ChannelError
import interfaces.ChannelServicesInterface
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.*
import transactionManager.TransactionManager
import org.junit.jupiter.api.BeforeEach
import utils.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ChannelServicesTest {

	private lateinit var transactionManager: TransactionManager
	private lateinit var channelServices: ChannelServicesInterface

	@BeforeEach
	fun setup() {
		transactionManager = mockk()
		channelServices = ChannelServices(transactionManager)
	}

	@Test
	fun `create a new channel`() {
		val owner = 1u
		val ownerName = "owner"
		val name = "name"
		val accessControl = "READ_WRITE"
		val visibility = "PUBLIC"
		every { transactionManager.run<Either<Channel, Channel>>(any()) } returns success(
			Channel.createChannel(
				1u,
				UserInfo(owner, ownerName),
				ChannelName(name, ownerName),
				AccessControl.valueOf(accessControl),
				Visibility.valueOf(visibility)
			)
		)
		val response = channelServices.createChannel(owner, name, accessControl, visibility)
		verify { transactionManager.run<Either<Channel, Channel>>(any()) }
		assertIs<Success<Channel>>(response)
		val channel = response.value
		assertEquals(1u, channel.id)
		assertEquals(owner, channel.owner.uId)
		assertEquals("@${ownerName}/$name", channel.name.fullName)
		assertEquals(AccessControl.READ_WRITE, channel.accessControl)
	}

	@Test
	fun `delete a channel`() {
		val id = 1u
		every { transactionManager.run<Either<ChannelError, Unit>>(any()) } returns success(Unit)
		channelServices.deleteChannel(id)
		verify { transactionManager.run<Either<ChannelError, Unit>>(any()) }
	}

	@Test
	fun `fail to create a channel due to blank name`() {
		val owner = 1u
		val name = ""
		val accessControl = "READ_WRITE"
		val visibility = "PUBLIC"
		every { transactionManager.run<Either<ChannelError, Channel>>(any()) } returns
			failure(ChannelError.InvalidChannelInfo)
		val response = channelServices.createChannel(owner, name, accessControl, visibility)
		verify(inverse = true) { transactionManager.run<Either<Channel, Channel>>(any()) }
		assertIs<Failure<ChannelError>>(response)
	}

	@Test
	fun `fail to create a channel due to blank access control`() {
		val owner = 1u
		val name = "name"
		val accessControl = ""
		val visibility = "PUBLIC"
		every { transactionManager.run<Either<ChannelError, Channel>>(any()) } returns
			failure(ChannelError.InvalidChannelInfo)
		val response = channelServices.createChannel(owner, name, accessControl, visibility)
		verify(inverse = true) { transactionManager.run<Either<Channel, Channel>>(any()) }
		assertIs<Failure<ChannelError>>(response)
	}

	@Test
	fun `fail to create a channel due to blank visibility`() {
		val owner = 1u
		val name = "name"
		val accessControl = "READ_WRITE"
		val visibility = ""
		every { transactionManager.run<Either<ChannelError, Channel>>(any()) } returns
			failure(ChannelError.InvalidChannelInfo)
		val response = channelServices.createChannel(owner, name, accessControl, visibility)
		verify(inverse = true) { transactionManager.run<Either<Channel, Channel>>(any()) }
		assertIs<Failure<ChannelError>>(response)
	}
}
