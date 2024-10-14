package interfaces

import errors.ChannelError
import errors.Error
import model.messages.Message
import utils.Either

/**
 * Represents the services available for the user entity.
 */
interface MessageServicesInterface {
    /**
     * Creates a new message.
     * @param msg The message info to create.
     * @return The created [Message].
     */
    fun createMessage(msg: Message): Either<Error, Message>

    /**
     * Deletes a user.
     * @param id The id of the message to delete.
     */
    fun deleteMessage(id: UInt): Either<Error, String>

    /**
     * Gets a message by its id.
     * @param id The id of the message to get.
     */
    fun getMessage(id: UInt): Either<Error, Message>

    /**
     * Gets the latest messages of a channel.
     * @param id The id of the channel.
     * @param limit The quantity of messages to get.
     */
    fun latestMessages(
        id: UInt,
        offset: Int,
        limit: Int,
    ): Either<ChannelError, List<Message>>
}