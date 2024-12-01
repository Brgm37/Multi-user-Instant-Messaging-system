package interfaces

import errors.MessageError
import model.messages.Message
import utils.Either
import java.sql.Timestamp

private const val MSG_OFFSET = 0u
private const val MSG_LIMIT = 100u

/**
 * Represents the services available for the user entity.
 */
interface MessageServicesInterface {
    /**
     * Creates a new message.
     * @param msg The message to create.
     * @param user The user that created the message.
     * @param channel The channel in which the message is present.
     */
    fun createMessage(
        msg: String,
        user: UInt,
        channel: UInt,
    ): Either<MessageError, Message>

    /**
     * Deletes a user.
     * @param msgId The id of the message to delete.
     * @param uId The id of the user trying to delete the message.
     */
    fun deleteMessage(
        msgId: UInt,
        uId: UInt,
    ): Either<MessageError, Unit>

    /**
     * Gets a message by its id.
     * @param msgId The id of the message to get.
     * @param uId The id of the user trying to access the message.
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
        offset: UInt = MSG_OFFSET,
        limit: UInt = MSG_LIMIT,
    ): Either<MessageError, List<Message>>

    /**
     * Gets messages by timestamp.
     *
     * @param channelId The id of the channel.
     * @param timestamp The timestamp to get the messages from.
     * @param limit The quantity of messages to get.
     */
    fun messagesByTimeStamp(
        channelId: UInt,
        timestamp: Timestamp,
        limit: UInt = MSG_LIMIT,
    ): Either<MessageError, List<Message>>
}