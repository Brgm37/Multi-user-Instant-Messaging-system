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
                is Channel.Public -> channel.copy(cId = nextId++)
                is Channel.Private -> channel.copy(cId = nextId++)
            }
        channels.add(newChannel)
        val cId = checkNotNull(newChannel.cId) { "Channel ID is null" }
        usersInChannels.getOrPut(cId) {
            mutableListOf(
                Pair(newChannel.owner.uId, AccessControl.READ_WRITE),
            )
        }
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
        if (!channels.any { it.cId == channelId }) {
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

    override fun findInvitation(channelId: UInt): ChannelInvitation? = invitations.find { it.cId == channelId }

    override fun updateInvitation(invitation: ChannelInvitation) {
        invitations.removeIf { it.cId == invitation.cId }
        invitations.add(invitation)
    }

    override fun deleteInvitation(channelId: UInt) {
        invitations.removeIf { it.cId == channelId }
    }

    override fun createInvitation(invitation: ChannelInvitation) {
        invitations.add(invitation)
    }

    override fun findUserAccessControl(
        channelId: UInt,
        userId: UInt,
    ): AccessControl? = usersInChannels[channelId]?.find { it.first == userId }?.second

    override fun findByName(name: String): Channel? {
        return channels.find { it.name.fullName == name }
    }

    override fun findByName(
        name: String,
        offset: UInt,
        limit: UInt,
    ): List<Channel> {
        return channels.filter { it.name.fullName.contains(name) }
    }

    override fun findById(id: UInt): Channel? = channels.find { it.cId == id }

    override fun findAll(
        offset: UInt,
        limit: UInt,
    ): List<Channel> =
        channels
            .filterIsInstance<Channel.Public>()
            .drop(offset.toInt())
            .take(limit.toInt())

    override fun save(entity: Channel) {
        channels.removeIf { it.cId == entity.cId }
        channels.add(entity)
    }

    override fun deleteById(id: UInt) {
        channels.removeIf { it.cId == id }
    }

    override fun clear() {
        channels.clear()
        usersInChannels.clear()
        invitations.clear()
    }
}