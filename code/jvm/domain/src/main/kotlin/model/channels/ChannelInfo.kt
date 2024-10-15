package model.channels

/**
 * Represents a Channel.
 *
 * @param channelId the channel’s identifier (unique).
 * @param channelName the username of the user.
 * @throws IllegalArgumentException if the username is empty.
 */
data class ChannelInfo(
    val channelId: UInt,
    val channelName: ChannelName,
) {
    init {
        require(channelName.fullName.isNotBlank()) { "The channel name cannot be blank." }
    }
}