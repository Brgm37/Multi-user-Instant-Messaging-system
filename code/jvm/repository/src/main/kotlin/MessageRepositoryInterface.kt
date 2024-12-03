import model.messages.Message
import java.sql.Timestamp

/**
 * Interface for the channel repository
 */

interface MessageRepositoryInterface : Repository<Message> {
    /**
     * Creates a new message
     * @param message The message to create
     * @return The created message with its ID
     */
    fun createMessage(message: Message): Message?

    /**
     * Finds limit amount of messages by channel ID
     * @param channelId The ID of the channel
     * @param limit The maximum number of messages to retrieve
     * @param offset The offset to start retrieving messages
     * @return A list with all messages in the channel
     */
    fun findMessagesByChannelId(
        channelId: UInt,
        limit: UInt,
        offset: UInt,
    ): List<Message>

    /**
     * Emits all messages to the user
     * @param uId The user ID
     * @param lastEventId The last event ID
     * @param emitter The emitter function
     */
    fun emitAllMessages(
        uId: UInt,
        lastEventId: UInt,
        emitter: (Message) -> Unit,
    )

    /**
     * Finds messages by timestamp
     * @param channelId The ID of the channel
     * @param timestamp The timestamp to search for
     * @param limit The maximum number of messages to retrieve
     * @param isBefore Whether to search for messages before or after the timestamp
     * @return A [List] with all [Message] in the channel
     */
    fun findMessagesByTimeStamp(
        channelId: UInt,
        timestamp: Timestamp?,
        limit: UInt,
        isBefore: Boolean,
    ): List<Message>
}