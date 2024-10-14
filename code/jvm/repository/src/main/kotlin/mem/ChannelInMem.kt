package mem

import ChannelRepositoryInterface
import model.channels.AccessControl
import model.channels.Channel
import model.channels.ChannelInvitation

/**
 * In-memory implementation of the channel repository
 */
class ChannelInMem : ChannelRepositoryInterface {
    private val channels = mutableListOf<Channel>()
    private val usersInChannels = mutableMapOf<UInt, MutableList<Pair<UInt, AccessControl>>>()
    private val invitations = mutableListOf<ChannelInvitation>()
    private var nextId = 1u

    override fun createChannel(channel: Channel): Channel {
        val newChannel =
            when (channel) {
                is Channel.Public -> channel.copy(channelId = nextId++)
                is Channel.Private -> channel.copy(channelId = nextId++)
            }
        channels.add(newChannel)
        return newChannel
    }

    override fun findByUserId(
        userId: UInt,
        offset: Int,
        limit: Int,
    ): List<Channel> = channels.filter { it.owner.uId == userId }

    override fun joinChannel(
        channelId: UInt,
        userId: UInt,
        accessControl: AccessControl,
    ) {
        if (!channels.any { it.channelId == channelId }) {
            return
        }
        usersInChannels
            .getOrPut(channelId) { mutableListOf() }
            .add(userId to accessControl)
    }

    override fun isUserInChannel(
        channelId: UInt,
        userId: UInt,
    ): Boolean = usersInChannels[channelId]?.any { it.first == userId } ?: false

    override fun findInvitation(channelId: UInt): ChannelInvitation? = invitations.find { it.channelId == channelId }

    override fun updateInvitation(invitation: ChannelInvitation) {
        invitations.removeIf { it.channelId == invitation.channelId }
        invitations.add(invitation)
    }

    override fun deleteInvitation(channelId: UInt) {
        invitations.removeIf { it.channelId == channelId }
    }

    override fun createInvitation(invitation: ChannelInvitation) {
        invitations.add(invitation)
    }

    override fun findById(id: UInt): Channel? = channels.find { it.channelId == id }

    override fun findAll(
        offset: Int,
        limit: Int,
    ): List<Channel> = channels.filterIsInstance<Channel.Public>()

    override fun save(entity: Channel) {
        channels.removeIf { it.channelId == entity.channelId }
        channels.add(entity)
    }

    override fun deleteById(id: UInt) {
        channels.removeIf { it.channelId == id }
    }

    override fun clear() {
        channels.clear()
        usersInChannels.clear()
        invitations.clear()
    }
}