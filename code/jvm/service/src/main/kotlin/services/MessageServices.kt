package services

import MessageRepositoryInterface
import errors.Error
import interfaces.MessageServicesInterface
import model.messages.Message
import utils.Either
import utils.failure
import utils.success

class MessageServices(
	// private val userRepo: UserRepositoryInterface,
	private val messageRepo: MessageRepositoryInterface,
) : MessageServicesInterface {
	override fun createMessage(msg: Message): Either<Error, Message> {
		require(msg.msg.isNotBlank()) { "msg cannot be blank" }
		require(msg.user.username.isNotBlank()) { "username cannot be blank" }
		require(
			msg.channel.channelName.fullName
				.isNotBlank(),
		) { "channel name cannot be blank" }
		val createdMessage = messageRepo.createMessage(msg)
		return success(createdMessage)
	}

	override fun deleteMessage(id: UInt): Either<Error, String> {
		messageRepo.deleteById(id)
		return success("Message deleted successfully")
	}

	override fun getMessage(id: UInt): Either<Error, Message> {
		val msg = messageRepo.findById(id) ?: return failure(Error.MessageNotFound)
		return success(msg)
	}
}
