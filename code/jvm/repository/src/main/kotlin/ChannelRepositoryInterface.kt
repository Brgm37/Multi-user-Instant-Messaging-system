import model.Channel

/**
 * Interface for the channel repository
 */
interface ChannelRepositoryInterface : Repository<Channel> {
	/**
	 * Creates a new channel
	 * @param channel The channel to create
	 * @return The created channel with its ID
	 */
	fun createChannel(channel: Channel): Channel

	/**
	 * Retrieves all channels owned by a user
	 * @param userId The ID of the user
	 * @return A list with all channels owned by the user
	 */
	fun findByUserId(userId: UInt): List<Channel>
}