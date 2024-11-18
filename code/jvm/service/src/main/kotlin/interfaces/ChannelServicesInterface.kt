package interfaces

import errors.ChannelError
import model.channels.Channel
import utils.Either
import java.util.UUID

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
     * Gets all channels that owner owns.
     * @param owner The owner of the channels.
     */
    fun getChannels(
        owner: UInt,
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
    ): Either<ChannelError, UUID>

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
}