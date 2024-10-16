package services

import TransactionManager
import errors.ChannelError
import errors.ChannelError.ChannelNotFound
import errors.ChannelError.InvalidChannelAccessControl
import errors.ChannelError.InvalidChannelInfo
import errors.ChannelError.InvalidChannelVisibility
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
import model.users.UserInfo
import utils.Either
import utils.failure
import utils.success
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.UUID

/**
 * The offset for the channels.
 */
private const val OFFSET = 0u

/**
 * The limit for the channels.
 */
private const val LIMIT = 100u

@Named("ChannelServices")
class ChannelServices(
    private val repoManager: TransactionManager,
) : ChannelServicesInterface {
    override fun createChannel(
        owner: UInt,
        name: String,
        accessControl: String,
        visibility: String,
    ): Either<ChannelError, Channel> {
        if (name.isEmpty() || visibility.isEmpty() || accessControl.isEmpty()) {
            return failure(InvalidChannelInfo)
        }
        if (accessControl.uppercase() !in AccessControl.entries.map(AccessControl::name)) {
            return failure(InvalidChannelAccessControl)
        }
        if (visibility.uppercase() !in Visibility.entries.map(Visibility::name)) {
            return failure(InvalidChannelVisibility)
        }
        return repoManager.run {
            val user = userRepo.findById(owner) ?: return@run failure(UserNotFound)
            val id = checkNotNull(user.uId) { "User id is null" }
            val channel =
                createChannel(
                    owner = UserInfo(id, user.username),
                    name = ChannelName(name, user.username),
                    accessControl = AccessControl.valueOf(accessControl.uppercase()),
                    visibility = Visibility.valueOf(visibility.uppercase()),
                )
            channelRepo.createChannel(channel)?.let {
                success(it)
            } ?: failure(UnableToCreateChannel)
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
                val messages = messageRepo.findMessagesByChannelId(id, LIMIT, OFFSET)
                return@run when (channel) {
                    is Public -> success(channel.copy(messages = messages))
                    is Private -> success(channel.copy(messages = messages))
                }
            }

    override fun getChannels(
        owner: UInt,
        offset: UInt,
        limit: UInt,
    ): Either<ChannelError, List<Channel>> =
        repoManager
            .run {
                val user = userRepo.findById(owner) ?: return@run failure(UserNotFound)
                val id = checkNotNull(user.uId) { "User id is null" }
                val channels = channelRepo.findByUserId(id, offset.toInt(), limit.toInt())
                success(channels)
            }

    override fun getChannels(
        offset: UInt,
        limit: UInt,
    ): Either<ChannelError, List<Channel>> =
        repoManager
            .run {
                val channels = channelRepo.findAll(offset.toInt(), limit.toInt())
                success(channels)
            }

    override fun createChannelInvitation(
        channelId: UInt,
        maxUses: UInt,
        expirationDate: String?,
        accessControl: String?,
        owner: UInt,
    ): Either<ChannelError, UUID> {
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
                if (channel.owner.uId != owner) {
                    return@run failure(InvalidChannelInfo)
                }
                val invitation =
                    if (accessControl == null) {
                        ChannelInvitation(
                            channelId = channelId,
                            expirationDate = timestamp.toLocalDateTime().toLocalDate(),
                            maxUses = maxUses,
                            accessControl = channel.accessControl,
                        )
                    } else {
                        ChannelInvitation(
                            channelId = channelId,
                            expirationDate = timestamp.toLocalDateTime().toLocalDate(),
                            maxUses = maxUses,
                            accessControl = AccessControl.valueOf(accessControl.uppercase()),
                        )
                    }
                channelRepo.createInvitation(invitation)
                success(invitation.invitationCode)
            }
    }

    private fun makeTimeStamp(expirationDate: String) =
        try {
            Timestamp.valueOf(expirationDate)
        } catch (e: IllegalArgumentException) {
            null
        }
}