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

@Named("MessageServices")
class MessageServices(
    private val repoManager: TransactionManager,
) : MessageServicesInterface {
    override fun createMessage(
        msg: String,
        user: UInt,
        channel: UInt,
    ): Either<MessageError, Message> {
        if (msg.isEmpty()) return failure(MessageError.InvalidMessageInfo)
        return repoManager.run {
            val msgChannel = channelRepo.findById(channel) ?: return@run failure(MessageError.ChannelNotFound)
            val channelId = checkNotNull(msgChannel.cId) { "Channel id is null" }
            val msgUser = userRepo.findById(user) ?: return@run failure(MessageError.UserNotFound)
            val uId = checkNotNull(msgUser.uId) { "User id is null" }
            val message =
                Message(
                    msg = msg,
                    user = UserInfo(uId, msgUser.username),
                    channel = ChannelInfo(channelId, msgChannel.name),
                )
            val userAccessControl =
                channelRepo.findUserAccessControl(channelId, uId) ?: return@run failure(MessageError.UserNotInChannel)
            if (msgChannel is Channel.Public &&
                msgChannel.accessControl == AccessControl.READ_ONLY &&
                msgChannel.owner.uId != uId
            ) {
                return@run failure(MessageError.UserDoesNotHaveAccess)
            }
            if (userAccessControl == AccessControl.READ_ONLY) return@run failure(MessageError.UserDoesNotHaveAccess)
            val createdMessage = messageRepo.createMessage(message)
            checkNotNull(createdMessage) { "Message is null" }
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
            if (!channelRepo.isUserInChannel(
                    message.channel.cId,
                    uId,
                )
            ) {
                return@run failure(MessageError.UserNotInChannel)
            }
            success(message)
        }

    override fun latestMessages(
        channelId: UInt,
        uId: UInt,
        offset: UInt,
        limit: UInt,
    ): Either<MessageError, List<Message>> {
        return repoManager.run {
            val channel = channelRepo.findById(channelId) ?: return@run failure(MessageError.ChannelNotFound)
            val chId = checkNotNull(channel.cId) { "Channel id is null" }
            if (!channelRepo.isUserInChannel(chId, uId)) return@run failure(MessageError.UserNotInChannel)
            val messages = messageRepo.findMessagesByChannelId(chId, limit, offset)
            success(messages)
        }
    }
}