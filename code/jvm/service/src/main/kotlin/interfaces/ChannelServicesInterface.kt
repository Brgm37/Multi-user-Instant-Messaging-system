package interfaces

import errors.ChannelError
import model.Channel
import model.Message
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
		visibility: String
	): Either<ChannelError, Channel>

	/**
	 * Deletes a channel.
	 * @param id The id of the channel to delete.
	 */
	fun deleteChannel(
		id: UInt
	): Either<ChannelError, Unit>

	/**
	 * Gets a channel by its id.
	 * @param id The id of the channel to get.
	 */
	fun getChannel(
		id: UInt
	): Either<ChannelError, Channel>

	/**
	 * Gets all channels that owner owns.
	 * @param owner The owner of the channels.
	 */
	fun getChannels(
		owner: UInt
	): Either<ChannelError, Sequence<Channel>>

	/**
	 * Gets all channels.
	 */
	fun getChannels(): Either<ChannelError, List<Channel>>

	/**
	 * Gets the latest messages of a channel.
	 * @param id The id of the channel.
	 * @param quantity The quantity of messages to get.
	 */
	fun latestMessages(
		id: UInt,
		quantity: Int
	): Either<ChannelError, List<Message>>
}