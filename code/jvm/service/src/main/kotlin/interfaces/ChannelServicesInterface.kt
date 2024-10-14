package interfaces

import errors.ChannelError
import model.channels.Channel
import utils.Either

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
     * @return The created [Channel].
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
        offset: Int,
        limit: Int,
    ): Either<ChannelError, List<Channel>>

    /**
     * Gets all channels.
     */
    fun getChannels(
        offset: Int,
        limit: Int,
    ): Either<ChannelError, List<Channel>>
}