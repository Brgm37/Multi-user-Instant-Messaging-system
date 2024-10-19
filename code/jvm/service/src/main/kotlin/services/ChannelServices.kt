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
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

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
            val createdChannel = channelRepo.createChannel(channel) ?: return@run failure(UnableToCreateChannel)
            val chId = checkNotNull(createdChannel.cId)
            channelRepo.joinChannel(chId, uId, AccessControl.READ_WRITE)
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
                channelRepo.createInvitation(invitation)
                success(invitation.invitationCode)
            }
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