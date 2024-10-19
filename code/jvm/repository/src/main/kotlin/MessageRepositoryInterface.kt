import model.messages.Message

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
}