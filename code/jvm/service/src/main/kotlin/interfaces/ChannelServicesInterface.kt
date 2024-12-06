package interfaces

import errors.ChannelError
import model.channels.AccessControl
import model.channels.Channel
import model.channels.ChannelInvitation
import utils.Either

/**
 * The offset for the channels.
 */
private const val OFFSET = 0u

/**
 * The limit for the channels.
 */
private const val LIMIT = 100u

/**
 * Represents the services available for the channel entity.
 */
interface ChannelServicesInterface {
    /**
     * Creates a new channel.
     * @param owner The owner of the channel.
     * @param name The name of the channel.
     * @param accessControl The access control of the channel.
     * @param visibility The visibility of the channel.
     */
    fun createChannel(
        owner: UInt,
        name: String,
        accessControl: String,
        visibility: String,
        description: String? = null,
        icon: String? = null,
    ): Either<ChannelError, Channel>

    /**
     * Deletes a channel.
     * @param id The id of the channel to delete.
     */
    fun deleteChannel(id: UInt): Either<ChannelError, Unit>

    /**
     * Gets a channel by its id.
     * @param id The id of the channel to get.
     */
    fun getChannel(id: UInt): Either<ChannelError, Channel>

    /**
     * Gets all channels that the user is part of.
     * @param user the user id.
     */
    fun getChannels(
        user: UInt,
        offset: UInt = OFFSET,
        limit: UInt = LIMIT,
    ): Either<ChannelError, List<Channel>>

    /**
     * Gets all channels.
     */
    fun getChannels(
        offset: UInt = OFFSET,
        limit: UInt = LIMIT,
    ): Either<ChannelError, List<Channel>>

    /**
     * Creates a new channel invitation.
     * @param channelId The id of the channel.
     * @param maxUses The maximum uses of the invitation.
     * @param expirationDate The expiration date of the invitation.
     * @param accessControl The access control of the invitation.
     * @param owner The owner of the invitation.
     */
    fun createChannelInvitation(
        channelId: UInt,
        maxUses: UInt,
        expirationDate: String?,
        accessControl: String?,
        owner: UInt,
    ): Either<ChannelError, ChannelInvitation>

    /**
     * Get a channel by its Name.
     *
     * @param name The name of the channel to get.
     */
    fun getByName(name: String): Either<ChannelError, Channel>

    /**
     * Get a channel list by its Name.
     *
     * @param name The name of the channel to get.
     * @param offset The offset for the channels.
     * @param limit The limit for the channels.
     */
    fun getByName(
        name: String,
        offset: UInt = OFFSET,
        limit: UInt = LIMIT,
    ): Either<ChannelError, List<Channel>>

    /**
     * Get a channel list witch the user is part of by its name.
     *
     * @param userId The user id.
     * @param name The name of the channel to get.
     * @param offset The offset for the channels.
     * @param limit The limit for the channels.
     */
    fun getByName(
        userId: UInt,
        name: String,
        offset: UInt = OFFSET,
        limit: UInt = LIMIT,
    ): Either<ChannelError, List<Channel>>

    /**
     * Updates a channel.
     * @param id The id of the channel to update.
     * @param name The new name of the channel.
     * @param accessControl The new access control of the channel.
     * @param visibility The new visibility of the channel.
     * @param description The new description of the channel.
     * @param icon The new icon of the channel.
     */
    fun updateChannel(
        id: UInt,
        name: String?,
        accessControl: String?,
        visibility: String?,
        description: String?,
        icon: String?,
    ): Either<ChannelError, Channel>

    /**
     * Get the access control of a user into a given channel.
     *
     * @param uId the id of the user.
     * @param cId the id of the channel.
     *
     * @return [Either] either a [ChannelError] if an error occurs,
     * or [AccessControl] the access control of the user in the channel.
     * Null is returned if the user is not in the channel.
     */
    fun getAccessControl(
        uId: UInt,
        cId: UInt,
    ): Either<ChannelError, AccessControl?>

    /**
     * Associates a user to a channel.
     * @param uId The id of the user to join the channel.
     * @param cId The id of the channel to join.
     * @param invitationCode The invitation code to join the channel.
     */
    fun joinChannel(
        uId: UInt,
        cId: UInt?,
        invitationCode: String?,
    ): Either<ChannelError, Channel>

    /**
     * Retrieves all public channels witch the user is not in.
     *
     * @param uId The ID of the user.
     * @param offset The offset for the channels.
     * @param limit The maximum number of channels to retrieve.
     */
    fun getPublic(
        uId: UInt,
        offset: UInt = OFFSET,
        limit: UInt = LIMIT,
    ): Either<ChannelError, List<Channel>>
}