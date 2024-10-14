package services

import TransactionManager
import errors.MessageError
import errors.MessageError.ChannelNotFound
import errors.MessageError.EmptyMessageContent
import errors.MessageError.MessageNotFound
import errors.MessageError.UserHasNoWriteAccess
import errors.MessageError.UserNotFound
import errors.MessageError.UserNotInChannel
import interfaces.MessageServicesInterface
import jakarta.inject.Inject
import jakarta.inject.Named
import model.channels.AccessControl
import model.channels.ChannelInfo
import model.messages.Message
import model.users.UserInfo
import utils.Either
import utils.failure
import utils.success
import java.sql.Timestamp

@Named("MessageServices")
class MessageServices
@Inject
constructor(
    @Named("TransactionManagerJDBC") private val repoManager: TransactionManager,
) : MessageServicesInterface {
    override fun createMessage(
        msg: String,
        user: UInt,
        channel: UInt,
        creationTime: Timestamp,
    ): Either<MessageError, Message> {
        if (msg.isEmpty()) return failure(EmptyMessageContent)
        return repoManager.run {
            if (!channelRepo.isUserInChannel(user, channel)) {
                return@run failure(UserNotInChannel)
            }
            val channelAccess = channelRepo.findUserAccessControl(user, channel)
            if (channelAccess != AccessControl.READ_WRITE) {
                return@run failure(UserHasNoWriteAccess)
            }
            val msgUser = userRepo.findById(user) ?: return@run failure(UserNotFound)
            val uId = checkNotNull(msgUser.uId) { "User id is null" }
            val msgChannel = channelRepo.findById(channel) ?: return@run failure(ChannelNotFound)
            val channelId = checkNotNull(msgChannel.channelId) { "Channel id is null" }
            val message = Message(
                msg = msg,
                user = UserInfo(uId, msgUser.username),
                channel = ChannelInfo(channelId, msgChannel.name),
                creationTime = creationTime,
            )
            val createdMessage = messageRepo.createMessage(message)
            if (createdMessage == null) {
                failure(MessageError.UnableToCreateMessage)
            } else {
                success(createdMessage)
            }
        }

    }

    override fun deleteMessage(id: UInt): Either<MessageError, Unit> =
        repoManager.run {
            messageRepo.findById(id) ?: return@run failure(MessageNotFound)
            messageRepo.deleteById(id)
            success(Unit)
        }


    override fun getMessage(msgId: UInt, uId: UInt): Either<MessageError, Message> =
        repoManager.run {
            val message = messageRepo.findById(msgId) ?: return@run failure(MessageNotFound)
            val channel = channelRepo.findById(message.channel.channelId) ?: return@run failure(ChannelNotFound)
            val channelId = checkNotNull(channel.channelId) { "Channel id is null" }
            if (!channelRepo.isUserInChannel(uId, channelId)) {
                return@run failure(UserNotInChannel)
            }
            success(message)
        }

    override fun latestMessages(
        channelId: UInt,
        uId: UInt,
        offset: Int,
        limit: Int
    ): Either<MessageError, List<Message>> {
        return repoManager.run {
            if (!channelRepo.isUserInChannel(uId, channelId)) {
                return@run failure(UserNotInChannel)
            }
            val messages = messageRepo.findMessagesByChannelId(channelId, offset.toUInt(), limit.toUInt())
            success(messages)
        }
    }
}