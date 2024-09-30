package services

import model.AccessControl
import model.Channel
import model.ChannelName
import model.UserInfo
import services.database.ChannelDataBaseMock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


class ChannelServicesTest {
	@Test
	fun `create a new channel`() {
		val initialList = mutableListOf<Channel>()
		val id = 1u
		val channelServices = ChannelServices(ChannelDataBaseMock(id, initialList))
		val owner = "owner" to 1u
		val name = "name"
		val accessControl = "READ_WRITE"
		val visibility = "PUBLIC"
		val channel = channelServices.createChannel(owner, name, accessControl, visibility)
		assertEquals(id, channel.id)
	}

	@Test
	fun `delete a channel`() {
		val channel = Channel
			.Public(
				1u,
				UserInfo(1u, "name"),
				ChannelName("name", "name"),
				AccessControl.READ_WRITE
			)
		val initialList = mutableListOf<Channel>(channel)
		val id = 1u
		val channelServices = ChannelServices(ChannelDataBaseMock(id, initialList))
		channelServices.deleteChannel(id)
		assertEquals(0, channelServices.getChannels().count())
	}

	@Test
	fun `fail to create a channel due to blank name`() {
		val initialList = mutableListOf<Channel>()
		val id = 1u
		val channelServices = ChannelServices(ChannelDataBaseMock(id, initialList))
		val owner = "owner" to 1u
		val name = ""
		val accessControl = "READ_WRITE"
		val visibility = "PUBLIC"
		assertFailsWith<IllegalArgumentException> {
			channelServices.createChannel(owner, name, accessControl, visibility)
		}
	}

	@Test
	fun `fail to create a channel due to blank access control`() {
		val initialList = mutableListOf<Channel>()
		val id = 1u
		val channelServices = ChannelServices(ChannelDataBaseMock(id, initialList))
		val owner = "owner" to 1u
		val name = "name"
		val accessControl = ""
		val visibility = "PUBLIC"
		assertFailsWith<IllegalArgumentException> {
			channelServices.createChannel(owner, name, accessControl, visibility)
		}
	}

	@Test
	fun `fail to create a channel due to blank visibility`() {
		val initialList = mutableListOf<Channel>()
		val id = 1u
		val channelServices = ChannelServices(ChannelDataBaseMock(id, initialList))
		val owner = "owner" to 1u
		val name = "name"
		val accessControl = "READ_WRITE"
		val visibility = ""
		assertFailsWith<IllegalArgumentException> {
			channelServices.createChannel(owner, name, accessControl, visibility)
		}
	}
}