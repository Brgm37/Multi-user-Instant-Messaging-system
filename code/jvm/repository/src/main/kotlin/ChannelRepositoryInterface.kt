import model.Channel
import model.ChannelInvitation

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
	 * @param offset The offset to start retrieving channels
	 * @param limit The maximum number of channels to retrieve
	 * @return A list with all channels owned by the user
	 */
	fun findByUserId(
		userId: UInt,
		offset: Int,
		limit: Int,
	): List<Channel>

	/**
	 * Joins a user to a channel
	 * @param channelId The ID of the channel
	 * @param userId The ID of the user
	 */
	fun joinChannel(
		channelId: UInt,
		userId: UInt,
	)

	/**
	 * Checks if a user is in a channel
	 * @param channelId The ID of the channel
	 * @param userId The ID of the user
	 * @return True if the user is in the channel, false otherwise
	 */
	fun isUserInChannel(
		channelId: UInt,
		userId: UInt,
	): Boolean

	/**
	 * Retrieves a channel invitation associated with a channel
	 * @param channelId The ID of the channel
	 */
	fun findInvitation(channelId: UInt): ChannelInvitation?

	/**
	 * Updates a channel invitation
	 * @param channelId The ID of the channel
	 * @param invitation The updated invitation
	 */
	fun updateInvitation(
		channelId: UInt,
		invitation: ChannelInvitation,
	)

	/**
	 * Deletes a channel invitation
	 * @param channelId The ID of the channel
	 */
	fun deleteInvitation(channelId: UInt)

	/**
	 * Creates a channel invitation
	 * @param invitation The invitation to create
	 * @return The created invitation
	 */
	fun createInvitation(invitation: ChannelInvitation)
}
