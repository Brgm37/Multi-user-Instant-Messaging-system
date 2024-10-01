package services

import MessageRepositoryInterface
import UserRepositoryInterface
import errors.Error
import interfaces.MessageServicesInterface
import model.Message
import utils.Either
import utils.failure
import utils.success

class MessageServices(
	private val userRepo: UserRepositoryInterface,
	private val messageRepo: MessageRepositoryInterface
) : MessageServicesInterface {

	override fun createMessage(msg: Message): Either<Error, Message> {
		val createdMessage = messageRepo.createMessage(msg)
		return success(createdMessage)	}

	override fun deleteMessage(id: UInt): Either<Error, String> {
		messageRepo.deleteById(id)
		return success("Message deleted successfully")
	}

	override fun getMessage(id: UInt): Either<Error, Message> {
		val msg = messageRepo.findById(id) ?: return failure(Error.MessageNotFound)
		return success(msg)
	}

	override fun getUserMessages(userId: UInt): Either<Error, Sequence<Message>> {
		val user = userRepo.findById(userId) ?: return failure(Error.UserNotFound)
		TODO()
	}
}