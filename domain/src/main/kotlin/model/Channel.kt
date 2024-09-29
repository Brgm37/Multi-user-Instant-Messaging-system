package org.example.model

/**
 * A channel is a container for user messages.
 *
 * @property id The unique identifier of the channel.
 * @property owner The user that created the channel.
 * @property name The name of the channel.
 * @property accessControl The access control settings of the channel.
 */
sealed interface Channel {
    val id: UInt
    val owner: User
    val name: ChannelName
    val accessControl: AccessControl

    /**
     * A public channel is visible to all users.
     *
     * @property id The unique identifier of the channel.
     * @property owner The user that created the channel.
     * @property name The name of the channel.
     * @property accessControl The access control settings of the channel.
     */
    data class Public(
        override val id: UInt,
        override val owner: User,
        override val name: ChannelName,
        override val accessControl: AccessControl,
    ) : Channel

    /**
     * A private channel is only visible to the owner
     * and the users invited to the channel by the owner.
     *
     * @property id The unique identifier of the channel.
     * @property owner The user that created the channel.
     * @property name The name of the channel.
     * @property accessControl The access control settings of the channel.
     */
    data class Private(
        override val id: UInt,
        override val owner: User,
        override val name: ChannelName,
        override val accessControl: AccessControl,
    ) : Channel
}
