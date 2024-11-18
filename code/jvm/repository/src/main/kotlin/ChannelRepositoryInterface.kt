import model.channels.AccessControl
import model.channels.Channel
import model.channels.ChannelInvitation

/**
 * The offset for the channels
 */
private const val OFFSET = 0u

/**
 * The limit for the channels
 */
private const val LIMIT = 100u

/**
 * Interface for the channel repository
 */
interface ChannelRepositoryInterface : Repository<Channel> {
    /**
     * Creates a new channel
     * @param channel The channel to create
     * @return The created channel with its ID
     */
    fun createChannel(channel: Channel): Channel?

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
     * @param accessControl The access control settings for the user
     */
    fun joinChannel(
        channelId: UInt,
        userId: UInt,
        accessControl: AccessControl,
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
     * @param invitation The updated invitation
     */
    fun updateInvitation(invitation: ChannelInvitation)

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

    /**
     * Retrieves the access control settings for a user in a channel
     * @param channelId The ID of the channel
     * @param userId The ID of the user
     * @return The access control settings for the user in the channel
     */
    fun findUserAccessControl(
        channelId: UInt,
        userId: UInt,
    ): AccessControl?

    /**
     * Retrieves a channel by its name
     * @param name The name of the channel
     * @return The channel with the given name
     */
    fun findByName(name: String): Channel?

    /**
     * Retrieves a channel list by its name
     *
     * @param name The name of the channel
     * @param offset The offset for the channels
     * @param limit The maximum number of channels to retrieve
     */
    fun findByName(
        name: String,
        offset: UInt = OFFSET,
        limit: UInt = LIMIT,
    ): List<Channel>
}