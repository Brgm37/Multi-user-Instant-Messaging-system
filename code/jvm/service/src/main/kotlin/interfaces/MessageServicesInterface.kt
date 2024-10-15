package interfaces

import errors.MessageError
import model.messages.Message
import utils.Either
import java.sql.Timestamp

/**
 * Represents the services available for the user entity.
 */
interface MessageServicesInterface {
    /**
     * Creates a new message.
     * @param msg The message to create.
     * @param user The user that created the message.
     * @param channel The channel in which the message is present.
     * @param creationTime The timestamp of when the message is created.
     */
    fun createMessage(
        msg: String,
        user: UInt,
        channel: UInt,
        creationTime: Timestamp,
    ): Either<MessageError, Message>

    /**
     * Deletes a user.
     * @param id The id of the message to delete.
     */
    fun deleteMessage(id: UInt): Either<MessageError, Unit>

    /**
     * Gets a message by its id.
     * @param msgId The id of the message to get.
     * @param uId The id of the user that created the message.
     */
    fun getMessage(
        msgId: UInt,
        uId: UInt,
    ): Either<MessageError, Message>

    /**
     * Gets limit amount of messages of a channel.
     * @param channelId The id of the channel.
     * @param uId The id of the user.
     * @param offset The offset of the messages.
     * @param limit The quantity of messages to get.
     */
    fun latestMessages(
        channelId: UInt,
        uId: UInt,
        offset: Int,
        limit: Int,
    ): Either<MessageError, List<Message>>
}