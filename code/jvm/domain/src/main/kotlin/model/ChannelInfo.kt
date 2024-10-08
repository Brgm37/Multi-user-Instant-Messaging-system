package model

/**
 * Represents a Channel.
 *
 * @param uId the channel’s identifier (unique).
 * @param username the username of the user.
 * @throws IllegalArgumentException if the username is empty.
 */
data class ChannelInfo(
	val uId: UInt,
	val channelname: ChannelName,
) {
	init {
		require(channelname.isNotBlank()) { "channelname must not be blank." }
	}
}
