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
	fun createMessage(message: Message): Message
}
