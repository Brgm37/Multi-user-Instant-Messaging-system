package model.channels

/**
 * Represents a Channel.
 *
 * @property cId the channel’s identifier (unique).
 * @property channelName the username of the user.
 * @throws IllegalArgumentException if the username is empty.
 */
data class ChannelInfo(
    val cId: UInt,
    val channelName: ChannelName,
) {
    init {
        require(channelName.fullName.isNotBlank()) { "The channel name cannot be blank." }
    }
}