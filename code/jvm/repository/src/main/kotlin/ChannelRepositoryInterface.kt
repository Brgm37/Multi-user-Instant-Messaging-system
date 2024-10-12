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
}
