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
        cId: UInt,
        uId: UInt,
        accessControl: AccessControl,
    ) {
        if (!channels.any { it.cId == cId }) {
            return
        }
        usersInChannels
            .getOrPut(cId) { mutableListOf() }
            .add(uId to accessControl)
    }

    override fun isUserInChannel(
        cId: UInt,
        uId: UInt,
    ): Boolean = usersInChannels[cId]?.any { it.first == uId } ?: false

    override fun findInvitation(cId: UInt): ChannelInvitation? = invitations.find { it.cId == cId }

    override fun updateInvitation(invitation: ChannelInvitation) {
        invitations.removeIf { it.cId == invitation.cId }
        invitations.add(invitation)
    }

    override fun deleteInvitation(cId: UInt) {
        invitations.removeIf { it.cId == cId }
    }

    override fun createInvitation(invitation: ChannelInvitation) {
        invitations.add(invitation)
    }

    override fun findUserAccessControl(
        cId: UInt,
        userId: UInt,
    ): AccessControl? = usersInChannels[cId]?.find { it.first == userId }?.second

    override fun findPublicByName(name: String): Channel? = channels.find { it.name.fullName == name }

    override fun findPublicByName(
        uId: UInt,
        name: String,
        offset: UInt,
        limit: UInt,
    ): List<Channel> = channels.filter { it.name.fullName.contains(name) }

    override fun findByName(
        uId: UInt,
        name: String,
        offset: UInt,
        limit: UInt,
    ): List<Channel> =
        channels.filter {
            it.name.fullName.contains(name) &&
                usersInChannels[it.cId]?.any { ac -> ac.first == uId } == true
        }

    override fun findByName(
        name: String,
        offset: UInt,
        limit: UInt,
    ): List<Channel> =
        channels.filter {
            it.name.fullName.contains(name)
        }

    override fun findAccessControl(
        uid: UInt,
        cId: UInt,
    ): AccessControl? = usersInChannels[cId]?.find { it.first == uid }?.second

    override fun findByInvitationCode(invitationCode: String): Channel? =
        invitations
            .find {
                it.invitationCode.toString() == invitationCode
            }?.let { findById(it.cId) }

    override fun findPublicChannel(
        uId: UInt,
        offset: UInt,
        limit: UInt,
    ): List<Channel> =
        channels.filter {
            it is Channel.Public && usersInChannels[it.cId]?.any { ac -> ac.first == uId } == false
        }

    override fun leaveChannel(
        cId: UInt,
        uId: UInt,
    ) {
        usersInChannels[cId]?.removeIf { it.first == uId }
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