package model

import model.channels.AccessControl.READ_ONLY
import model.channels.AccessControl.READ_WRITE
import model.channels.Channel
import model.channels.ChannelName
import model.channels.Visibility.PUBLIC
import model.users.UserInfo
import kotlin.test.Test
import kotlin.test.assertFailsWith

class ChannelTest {
    @Test
    fun `successful channel instantiation test`() {
        Channel.Public(
            channelId = 1u,
            owner = UserInfo(uId = 1u, username = "username"),
            name = ChannelName(name = "channelName", ownerName = "ownerName"),
            accessControl = READ_WRITE,
        )
    }

    @Test
    fun `exception generated by an invalid channel name test`() {
        assertFailsWith<IllegalArgumentException> {
            Channel.Private(
                channelId = 1u,
                owner = UserInfo(uId = 1u, username = "username"),
                name = ChannelName(name = "", ownerName = "ownerName"),
                accessControl = READ_WRITE,
            )
        }
    }

    @Test
    fun `successful channel instantiation test with null id`() {
        Channel.Private(
            channelId = null,
            owner = UserInfo(uId = 1u, username = "username"),
            name = ChannelName(name = "channelName", ownerName = "ownerName"),
            accessControl = READ_ONLY,
        )
    }

    @Test
    fun `createChannel test`() {
        val channel =
            Channel.createChannel(
                id = 1u,
                owner = UserInfo(uId = 1u, username = "username"),
                name = ChannelName(name = "channelName", ownerName = "ownerName"),
                accessControl = READ_WRITE,
                visibility = PUBLIC,
            )
        assert(channel is Channel.Public)
    }
}