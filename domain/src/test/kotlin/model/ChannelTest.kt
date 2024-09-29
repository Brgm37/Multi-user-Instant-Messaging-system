package model

import model.AccessControl.READ_ONLY
import model.AccessControl.READ_WRITE
import kotlin.test.Test
import kotlin.test.assertFailsWith

class ChannelTest {

	@Test
	fun `successful channel instantiation test`() {
		Channel.Public(
			id = 1u,
			owner = UserInfo(username = "username"),
			name = ChannelName(name = "channelName", ownerName = "ownerName"),
			accessControl = READ_WRITE
		)
	}

	@Test
	fun `exception generated by an invalid channel name test`() {
		assertFailsWith<IllegalArgumentException> {
			Channel.Private(
				id = 1u,
				owner = UserInfo(username = "username"),
				name = ChannelName(name = "", ownerName = "ownerName"),
				accessControl = READ_WRITE
			)
		}
	}

	@Test
	fun `successful channel instantiation test with null id`() {
		Channel.Private(
			id = null,
			owner = UserInfo(username = "username"),
			name = ChannelName(name = "channelName", ownerName = "ownerName"),
			accessControl = READ_ONLY
		)
	}
}