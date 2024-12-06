package services

import TransactionManager
import errors.ChannelError
import errors.ChannelError.ChannelNotFound
import errors.ChannelError.InvalidChannelAccessControl
import errors.ChannelError.InvalidChannelInfo
import errors.ChannelError.InvalidChannelVisibility
import errors.ChannelError.InvitationCodeHasExpired
import errors.ChannelError.InvitationCodeIsInvalid
import errors.ChannelError.UnableToCreateChannel
import errors.ChannelError.UserNotFound
import interfaces.ChannelServicesInterface
import jakarta.inject.Named
import model.channels.AccessControl
import model.channels.Channel
import model.channels.Channel.Companion.createChannel
import model.channels.Channel.Private
import model.channels.Channel.Public
import model.channels.ChannelInvitation
import model.channels.ChannelName
import model.channels.Visibility
import model.channels.decrementUses
import model.users.UserInfo
import utils.Either
import utils.failure
import utils.success
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * The offset for the channels message.
 */
private const val MSG_OFFSET = 0u

/**
 * The limit for the channels message.
 */
private const val MSG_LIMIT = 100u

/**
 * The services available for the channel entity.
 * @property repoManager The transaction manager.
 */
@Named("ChannelServices")
class ChannelServices(
    private val repoManager: TransactionManager,
) : ChannelServicesInterface {
    override fun createChannel(
        owner: UInt,
        name: String,
        accessControl: String,
        visibility: String,
        description: String?,
        icon: String?,
    ): Either<ChannelError, Channel> {
        val args: Array<String> = arrayOf(name, visibility, accessControl)
        if (args.any(String::isBlank)) return failure(InvalidChannelInfo)
        if (!AccessControl.validate(accessControl)) return failure(InvalidChannelAccessControl)
        val upperCaseVisibility = visibility.uppercase()
        if (!Visibility.validate(visibility)) return failure(InvalidChannelVisibility)
        val upperCaseAccessControl = accessControl.uppercase()
        return repoManager.run {
            val user = userRepo.findById(owner) ?: return@run failure(UserNotFound)
            val uId = checkNotNull(user.uId) { "User id is null" }
            val channel =
                createChannel(
                    owner = UserInfo(uId, user.username),
                    name = ChannelName(name, user.username),
                    accessControl = AccessControl.valueOf(upperCaseAccessControl),
                    visibility = Visibility.valueOf(upperCaseVisibility),
                )
            val checkChannel = channelRepo.findByName(channel.name.fullName)
            if (checkChannel != null) return@run failure(UnableToCreateChannel)
            val createdChannel = channelRepo.createChannel(channel) ?: return@run failure(UnableToCreateChannel)
            success(createdChannel)
        }
    }

    override fun deleteChannel(id: UInt): Either<ChannelError, Unit> =
        repoManager
            .run {
                channelRepo.findById(id) ?: return@run failure(ChannelNotFound)
                channelRepo.deleteById(id)
                success(Unit)
            }

    override fun getChannel(id: UInt): Either<ChannelError, Channel> =
        repoManager
            .run {
                val channel = channelRepo.findById(id) ?: return@run failure(ChannelNotFound)
                val messages = messageRepo.findMessagesByChannelId(id, MSG_LIMIT, MSG_OFFSET)
                return@run when (channel) {
                    is Public -> success(channel.copy(messages = messages))
                    is Private -> success(channel.copy(messages = messages))
                }
            }

    override fun getChannels(
        user: UInt,
        offset: UInt,
        limit: UInt,
    ): Either<ChannelError, List<Channel>> =
        repoManager
            .run {
                val u = userRepo.findById(user) ?: return@run failure(UserNotFound)
                val id = checkNotNull(u.uId) { "User id is null" }
                val channels = channelRepo.findByUserId(id, offset.toInt(), limit.toInt())
                success(channels)
            }

    override fun getChannels(
        offset: UInt,
        limit: UInt,
    ): Either<ChannelError, List<Channel>> =
        repoManager
            .run {
                val channels = channelRepo.findAll(offset, limit)
                success(channels)
            }

    override fun createChannelInvitation(
        channelId: UInt,
        maxUses: UInt,
        expirationDate: String?,
        accessControl: String?,
        owner: UInt,
    ): Either<ChannelError, ChannelInvitation> {
        val timestamp =
            if (expirationDate != null) {
                makeTimeStamp(expirationDate) ?: return failure(InvalidChannelInfo)
            } else {
                LocalDateTime
                    .now()
                    .plusWeeks(1)
                    .let(Timestamp::valueOf)
            }
        return repoManager
            .run {
                val channel = channelRepo.findById(channelId) ?: return@run failure(ChannelNotFound)
                if (channel.owner.uId != owner) return@run failure(InvalidChannelInfo)
                val invitation =
                    if (accessControl == null) {
                        ChannelInvitation(
                            cId = channelId,
                            expirationDate = timestamp,
                            maxUses = maxUses,
                            accessControl = channel.accessControl,
                        )
                    } else {
                        ChannelInvitation(
                            cId = channelId,
                            expirationDate = timestamp,
                            maxUses = maxUses,
                            accessControl = AccessControl.valueOf(accessControl.uppercase()),
                        )
                    }
                val oldInvitation = channelRepo.findInvitation(channelId)
                if (oldInvitation != null) channelRepo.deleteInvitation(channelId)
                channelRepo.createInvitation(invitation)
                success(invitation)
            }
    }

    override fun getByName(name: String): Either<ChannelError, Channel> =
        repoManager
            .run {
                val channel = channelRepo.findByName(name) ?: return@run failure(ChannelNotFound)
                val cId = checkNotNull(channel.cId) { "Channel id is null" }
                val messages = messageRepo.findMessagesByChannelId(cId, MSG_LIMIT, MSG_OFFSET)
                return@run when (channel) {
                    is Public -> success(channel.copy(messages = messages))
                    is Private -> success(channel.copy(messages = messages))
                }
            }

    override fun getByName(
        name: String,
        offset: UInt,
        limit: UInt,
    ): Either<ChannelError, List<Channel>> =
        repoManager
            .run {
                val channels = channelRepo.findByName(name, offset, limit)
                success(channels)
            }

    override fun getByName(
        userId: UInt,
        name: String,
        offset: UInt,
        limit: UInt,
    ): Either<ChannelError, List<Channel>> =
        repoManager.run {
            val user = userRepo.findById(userId) ?: return@run failure(UserNotFound)
            val uId = checkNotNull(user.uId) { "User id is null" }
            val channels = channelRepo.findByName(uId, name, offset, limit)
            success(channels)
        }

    override fun updateChannel(
        id: UInt,
        name: String?,
        accessControl: String?,
        visibility: String?,
        description: String?,
        icon: String?,
    ): Either<ChannelError, Channel> {
        val args = arrayOf(name, accessControl, visibility, icon)
        args.all { it.isNullOrBlank() }
        accessControl?.let { if (!AccessControl.validate(it)) return failure(InvalidChannelAccessControl) }
        visibility?.let { if (!Visibility.validate(it)) return failure(InvalidChannelVisibility) }
        val newDescription = description?.ifBlank { null }
        val upperCaseAccessControl = accessControl?.uppercase()
        return repoManager.run {
            val channel = channelRepo.findById(id) ?: return@run failure(ChannelNotFound)
            val updatedChannel =
                when (channel) {
                    is Public -> {
                        channel.copy(
                            name = name?.let { ChannelName(it, channel.owner.username) } ?: channel.name,
                            accessControl =
                                upperCaseAccessControl?.let { AccessControl.valueOf(it) }
                                    ?: channel.accessControl,
                            description = newDescription ?: channel.description,
                            icon = icon ?: channel.icon,
                        )
                    }

                    is Private -> {
                        channel.copy(
                            name = name?.let { ChannelName(it, channel.owner.username) } ?: channel.name,
                            accessControl =
                                upperCaseAccessControl?.let { AccessControl.valueOf(it) }
                                    ?: channel.accessControl,
                            description = newDescription ?: channel.description,
                            icon = icon ?: channel.icon,
                        )
                    }
                }
            channelRepo.save(updatedChannel)
            success(updatedChannel)
        }
    }

    override fun getAccessControl(
        uId: UInt,
        cId: UInt,
    ): Either<ChannelError, AccessControl?> =
        repoManager.run {
            userRepo.findById(uId) ?: return@run failure(UserNotFound)
            channelRepo.findById(cId) ?: return@run failure(ChannelNotFound)
            success(channelRepo.findAccessControl(uId, cId))
        }

    override fun joinChannel(
        uId: UInt,
        cId: UInt?,
        invitationCode: String?,
    ): Either<ChannelError, Channel> =
        repoManager.run {
            val args = arrayOf(cId, invitationCode)
            if (args.all { it == null }) return@run failure(InvalidChannelInfo)
            val channel =
                cId?.let { channelRepo.findById(it) } ?: invitationCode?.let { channelRepo.findByInvitationCode(it) }
                    ?: return@run failure(ChannelNotFound)
            userRepo.findById(uId) ?: return@run failure(UserNotFound)
            val validCId = checkNotNull(channel.cId) { "Channel id is null" }
            if (channelRepo.isUserInChannel(validCId, uId)) return@run success(channel)
            if (channel is Public) {
                channelRepo.joinChannel(validCId, uId, channel.accessControl)
                return@run success(channel)
            }
            val invitation = channelRepo.findInvitation(validCId) ?: return@run failure(InvitationCodeIsInvalid)
            if (invitationCode != invitation.invitationCode.toString()) return@run failure(InvitationCodeIsInvalid)
            if (invitation.isExpired || invitation.maxUses == 0u) {
                channelRepo.deleteInvitation(validCId)
                return@run failure(InvitationCodeHasExpired)
            }
            val decreasedInvitation = invitation.decrementUses()
            channelRepo.updateInvitation(decreasedInvitation)
            channelRepo.joinChannel(validCId, uId, invitation.accessControl)
            if (decreasedInvitation.maxUses == 0u) channelRepo.deleteInvitation(validCId)
            success(channel)
        }

    override fun getPublic(
        uId: UInt,
        offset: UInt,
        limit: UInt,
    ): Either<ChannelError, List<Channel>> =
        repoManager.run {
            userRepo.findById(uId) ?: return@run failure(UserNotFound)
            val channels = channelRepo.findPublicChannel(uId, offset, limit)
            success(channels)
        }

    private fun makeTimeStamp(expirationDate: String) =
        try {
            Timestamp.valueOf(
                LocalDate
                    .parse(expirationDate)
                    .atStartOfDay(),
            )
        } catch (e: IllegalArgumentException) {
            null
        }
}