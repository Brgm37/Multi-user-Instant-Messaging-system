package services

import TransactionManager
import errors.MessageError
import interfaces.MessageServicesInterface
import jakarta.inject.Named
import model.channels.AccessControl
import model.channels.Channel
import model.channels.ChannelInfo
import model.messages.Message
import model.users.UserInfo
import utils.Either
import utils.failure
import utils.success
import java.sql.Timestamp

@Named("MessageServices")
class MessageServices(
    private val repoManager: TransactionManager,
) : MessageServicesInterface {
    override fun createMessage(
        msg: String,
        user: UInt,
        channel: UInt,
        creationTime: String,
    ): Either<MessageError, Message> {
        if (msg.isEmpty()) return failure(MessageError.InvalidMessageInfo)
        return repoManager.run {
            val msgChannel = channelRepo.findById(channel) ?: return@run failure(MessageError.ChannelNotFound)
            val channelId = checkNotNull(msgChannel.cId) { "Channel id is null" }
            val msgUser = userRepo.findById(user) ?: return@run failure(MessageError.UserNotFound)
            val uId = checkNotNull(msgUser.uId) { "User id is null" }
            val timeOfCreation = Timestamp.valueOf(creationTime) ?: return@run failure(MessageError.InvalidMessageInfo)
            val message =
                Message(
                    msg = msg,
                    user = UserInfo(uId, msgUser.username),
                    channel = ChannelInfo(channelId, msgChannel.name),
                    creationTime = timeOfCreation,
                )
            val createdMessage =
                messageRepo.createMessage(message) ?: return@run failure(MessageError.UnableToCreateMessage)
            val userAccessControl =
                channelRepo.findUserAccessControl(channelId, uId) ?: return@run failure(MessageError.UserNotInChannel)
            if (msgChannel is Channel.Public &&
                msgChannel.accessControl == AccessControl.READ_ONLY &&
                msgChannel.owner.uId != uId
            ) {
                return@run failure(MessageError.UserDoesNotHaveAccess)
            }
            if (msgChannel is Channel.Public) {
                return@run success(createdMessage)
            }
            if (userAccessControl == AccessControl.READ_ONLY) {
                return@run failure(MessageError.UserDoesNotHaveAccess)
            }
            success(createdMessage)
        }
    }

    override fun deleteMessage(
        msgId: UInt,
        uId: UInt,
    ): Either<MessageError, Unit> =
        repoManager.run {
            val message = messageRepo.findById(msgId) ?: return@run failure(MessageError.MessageNotFound)
            val channel =
                channelRepo.findById(message.channel.cId) ?: return@run failure(MessageError.ChannelNotFound)
            val channelId = checkNotNull(channel.cId) { "Channel id is null" }
            if (!channelRepo.isUserInChannel(channelId, uId)) return@run failure(MessageError.UserNotInChannel)
            if (message.user.uId != uId && channel.owner.uId != uId) {
                return@run failure(MessageError.UserDoesNotHaveAccess)
            }
            messageRepo.deleteById(msgId)
            success(Unit)
        }

    override fun getMessage(
        msgId: UInt,
        uId: UInt,
    ): Either<MessageError, Message> =
        repoManager.run {
            val message = messageRepo.findById(msgId) ?: return@run failure(MessageError.MessageNotFound)
            val channel =
                channelRepo.findById(message.channel.cId) ?: return@run failure(MessageError.ChannelNotFound)
            val channelId = checkNotNull(channel.cId) { "Channel id is null" }
            if (!channelRepo.isUserInChannel(uId, channelId) && channel is Channel.Private) {
                return@run failure(MessageError.UserDoesNotHaveAccess)
            }
            success(message)
        }

    override fun latestMessages(
        channelId: UInt,
        uId: UInt,
        offset: Int,
        limit: Int,
    ): Either<MessageError, List<Message>> {
        return repoManager.run {
            val channel = channelRepo.findById(channelId) ?: return@run failure(MessageError.ChannelNotFound)
            val chId = checkNotNull(channel.cId) { "Channel id is null" }
            if (!channelRepo.isUserInChannel(uId, chId) && channel is Channel.Private) {
                return@run failure(MessageError.UserDoesNotHaveAccess)
            }
            val messages = messageRepo.findMessagesByChannelId(chId, offset.toUInt(), limit.toUInt())
            success(messages)
        }
    }
}