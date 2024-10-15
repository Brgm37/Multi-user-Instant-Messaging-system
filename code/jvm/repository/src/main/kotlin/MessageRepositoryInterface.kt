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
     * Finds a message by its channel id
     * @param channelId The id of the channel
     * @return The message with the given id.
     */
    fun findMessagesByChannelId(
        channelId: UInt,
        limit: UInt,
        offset: UInt,
    ): List<Message>
}