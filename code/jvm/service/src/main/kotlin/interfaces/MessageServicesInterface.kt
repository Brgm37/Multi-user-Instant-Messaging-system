package interfaces

import errors.Error
import model.Message
import model.User
import model.UserInfo
import utils.Either

/**
 * Represents the services available for the user entity.
 */
interface MessageServicesInterface {
	/**
	 * Creates a new mesage.
	 * @param msg The message info to create.
	 * @return The created [Message].
	 */
	fun createMessage(
		msg: Message
	): Either<Error, Message>

	/**
	 * Deletes a user.
	 * @param id The id of the message to delete.
	 */
	fun deleteMessage(
		id: UInt
	): Either<Error, String>

	/**
	 * Gets a message by its id.
	 * @param id The id of the message to get.
	 */
	fun getMessage(
		id: UInt
	): Either<Error, Message>

	TODO(relocate get latest messages)
}