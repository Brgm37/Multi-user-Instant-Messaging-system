package interfaces

import model.Channel
import model.Message

/**
 * - Represents the owner of a channel.
 * - The first element is the username of the owner.
 * - The second element is the id of the owner.
 */
typealias ownerInfo = Pair<String, UInt>

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
		owner: ownerInfo,
		name: String,
		accessControl: String,
		visibility: String
	): Channel

	/**
	 * Deletes a channel.
	 * @param id The id of the channel to delete.
	 */
	fun deleteChannel(
		id: UInt
	)

	/**
	 * Gets a channel by its id.
	 * @param id The id of the channel to get.
	 */
	fun getChannel(
		id: UInt
	): Channel

	/**
	 * Gets all channels that are owned by owner.
	 * @param owner The owner of the channels.
	 */
	fun getChannels(
		owner: UInt
	): Sequence<Channel>

	/**
	 * Gets all channels.
	 */
	fun getChannels(): Sequence<Channel>

	/**
	 * Gets the latest messages of a channel.
	 * @param id The id of the channel.
	 * @param quantity The quantity of messages to get.
	 */
	fun latestMessages(
		id: UInt,
		quantity: Int
	): Sequence<Message>
}