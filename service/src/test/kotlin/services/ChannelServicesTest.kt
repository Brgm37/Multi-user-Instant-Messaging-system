package services

import ChannelRepositoryInterface
import interfaces.ChannelServicesInterface
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.AccessControl
import model.Channel
import model.ChannelName
import model.UserInfo
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


class ChannelServicesTest {

	private lateinit var channelRepository: ChannelRepositoryInterface
	private lateinit var channelServices: ChannelServicesInterface

	@BeforeEach
	fun setup() {
		channelRepository = mockk()
		channelServices = ChannelServices(channelRepository)
	}

	@Test
	fun `create a new channel`() {
		val owner = "owner" to 1u
		val name = "name"
		val accessControl = "READ_WRITE"
		val visibility = "PUBLIC"
		every { channelRepository.createChannel(any()) } returns Channel.createChannel(
			1u,
			UserInfo(owner.second, owner.first),
			ChannelName(name, owner.first),
			AccessControl.READ_WRITE,
			visibility
		)
		val channel = channelServices.createChannel(owner, name, accessControl, visibility)
		assertEquals(1u, channel.id)
		assertEquals(owner.second, channel.owner.uId)
		assertEquals("@${owner.first}/$name", channel.name.fullName)
		assertEquals(AccessControl.READ_WRITE, channel.accessControl)
	}

	@Test
	fun `delete a channel`() {
		val id = 1u
		every { channelRepository.deleteById(id) } returns Unit
		channelServices.deleteChannel(id)
		verify { channelRepository.deleteById(id) }
	}

	@Test
	fun `fail to creat a channel due to blank name`() {
		val owner = "owner" to 1u
		val name = ""
		val accessControl = "READ_WRITE"
		val visibility = "PUBLIC"
		assertFailsWith<IllegalArgumentException> {
			channelServices.createChannel(owner, name, accessControl, visibility)
		}
	}

	@Test
	fun `fail to creat a channel due to blank access control`() {
		val owner = "owner" to 1u
		val name = "name"
		val accessControl = ""
		val visibility = "PUBLIC"
		assertFailsWith<IllegalArgumentException> {
			channelServices.createChannel(owner, name, accessControl, visibility)
		}
	}

	@Test
	fun `fail to creat a channel due to blank visibility`() {
		val owner = "owner" to 1u
		val name = "name"
		val accessControl = "READ_WRITE"
		val visibility = ""
		assertFailsWith<IllegalArgumentException> {
			channelServices.createChannel(owner, name, accessControl, visibility)
		}
	}
}