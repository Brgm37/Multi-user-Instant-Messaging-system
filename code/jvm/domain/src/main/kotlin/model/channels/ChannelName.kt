package model.channels

/**
 * The name of a channel.
 * The channel name is unique.
 * It is composed of the name of the user that created the channel and the channel's name.
 *
 * @property name The name of the channel.
 * @property ownerName The name of the user that created the channel.
 * @property fullName The full name of the channel, which includes the name of the user that created the channel.
 * @throws IllegalArgumentException If the channel name or the owner name is blank.
 */
data class ChannelName(
    val name: String,
    val ownerName: String,
) {
    init {
        require(name.isNotBlank()) { "The channel name cannot be blank." }
        require(ownerName.isNotBlank()) { "The owner name cannot be blank." }
    }

    val fullName: String by lazy { "@$ownerName/$name" }
}

/**
 * Converts a string to a channel name.
 *
 * @receiver The string to convert.
 * @return The channel name.
 */
fun String.toChannelName(): ChannelName {
    val (ownerName, name) = this.split("/")
    return ChannelName(name, ownerName.drop(1))
}