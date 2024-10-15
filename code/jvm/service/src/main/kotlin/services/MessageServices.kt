package services

import TransactionManager
import errors.MessageError
import errors.MessageError.ChannelNotFound
import errors.MessageError.InvalidMessageInfo
import errors.MessageError.MessageNotFound
import errors.MessageError.UserNotInChannel
import errors.MessageError.UserNotFound
import errors.MessageError.UserDoesNotHaveAccess
import interfaces.MessageServicesInterface
import jakarta.inject.Inject
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
class MessageServices
    @Inject
    constructor(
        @Named("TransactionManagerJDBC") private val repoManager: TransactionManager,
    ) : MessageServicesInterface {
        override fun createMessage(
            msg: String,
            user: UInt,
            channel: UInt,
            creationTime: String,
        ): Either<MessageError, Message> {
            if (msg.isEmpty()) return failure(InvalidMessageInfo)
            return repoManager.run {
                val msgChannel = channelRepo.findById(channel) ?: return@run failure(ChannelNotFound)
                val channelId = checkNotNull(msgChannel.channelId) { "Channel id is null" }
                val msgUser = userRepo.findById(user) ?: return@run failure(UserNotFound)
                val uId = checkNotNull(msgUser.uId) { "User id is null" }
                val timeOfCreation = Timestamp.valueOf(creationTime) ?: return@run failure(InvalidMessageInfo)
                val message =
                    Message(
                        msg = msg,
                        user = UserInfo(uId, msgUser.username),
                        channel = ChannelInfo(channelId, msgChannel.name),
                        creationTime = timeOfCreation,
                    )
                val createdMessage = messageRepo.createMessage(message)
                val userAccessControl = channelRepo.findUserAccessControl(uId, channelId) ?: return@run failure(UserNotInChannel)
                if (msgChannel is Channel.Public
                    && msgChannel.accessControl == AccessControl.READ_ONLY
                    && msgChannel.owner.uId != uId
                ) {
                    return@run failure(UserDoesNotHaveAccess)
                } else {
                    success(createdMessage)
                }
                if (userAccessControl == AccessControl.READ_ONLY) {
                    failure(UserDoesNotHaveAccess)
                }
                if (createdMessage == null) {
                    failure(MessageError.UnableToCreateMessage)
                } else {
                    success(createdMessage)
                }
            }
        }

        override fun deleteMessage(
            msgId: UInt,
            uId: UInt
        ): Either<MessageError, Unit> =
            repoManager.run {
                val message = messageRepo.findById(msgId) ?: return@run failure(MessageNotFound)
                val channel = channelRepo.findById(message.channel.channelId) ?: return@run failure(ChannelNotFound)
                val channelId = checkNotNull(channel.channelId) { "Channel id is null" }
                if(!channelRepo.isUserInChannel(uId, channelId)) return@run failure(UserNotInChannel)
                if(message.user.uId != uId || channel.owner.uId != uId) {
                    return@run failure(UserDoesNotHaveAccess)
                }
                messageRepo.deleteById(msgId)
                success(Unit)
            }

        override fun getMessage(
            msgId: UInt,
            uId: UInt,
        ): Either<MessageError, Message> =
            repoManager.run {
                val message = messageRepo.findById(msgId) ?: return@run failure(MessageNotFound)
                val channel = channelRepo.findById(message.channel.channelId) ?: return@run failure(ChannelNotFound)
                val channelId = checkNotNull(channel.channelId) { "Channel id is null" }
                if (!channelRepo.isUserInChannel(uId, channelId) && channel is Channel.Private) {
                    return@run failure(UserDoesNotHaveAccess)
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
                val channel = channelRepo.findById(channelId) ?: return@run failure(ChannelNotFound)
                val chId = checkNotNull(channel.channelId) { "Channel id is null" }
                if (!channelRepo.isUserInChannel(uId, chId) && channel is Channel.Private) {
                    return@run failure(UserDoesNotHaveAccess)
                }
                val messages = messageRepo.findMessagesByChannelId(chId, offset.toUInt(), limit.toUInt())
                success(messages)
            }
        }
    }