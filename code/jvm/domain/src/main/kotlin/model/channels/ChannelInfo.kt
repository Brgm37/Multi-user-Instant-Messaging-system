package model.channels

/**
 * Represents a Channel.
 *
 * @param uId the channel’s identifier (unique).
 * @param channelName the username of the user.
 * @throws IllegalArgumentException if the username is empty.
 */
data class ChannelInfo(
	val uId: UInt,
	val channelName: ChannelName,
)
