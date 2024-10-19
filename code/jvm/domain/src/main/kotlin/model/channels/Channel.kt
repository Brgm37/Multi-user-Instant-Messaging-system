package model.channels

import model.messages.Message
import model.users.UserInfo

/**
 * A channel is a container for user messages.
 *
 * @property cId The unique identifier of the channel.
 * @property owner The user that created the channel.
 * @property name The name of the channel.
 * @property accessControl The access control settings of the channel.
 */
sealed interface Channel {
    val cId: UInt?
    val owner: UserInfo
    val name: ChannelName
    val accessControl: AccessControl
    val messages: List<Message>

    /**
     * A public channel is visible to all users.
     *
     * @property cId The unique identifier of the channel.
     * @property owner The user that created the channel.
     * @property name The name of the channel.
     * @property accessControl The access control settings of the channel.
     */
    data class Public(
        override val cId: UInt? = null,
        override val owner: UserInfo,
        override val name: ChannelName,
        override val accessControl: AccessControl,
        override val messages: List<Message> = emptyList(),
    ) : Channel

    /**
     * A private channel is only visible to the owner
     * and the users invited to the channel by the owner.
     *
     * @property cId The unique identifier of the channel.
     * @property owner The user that created the channel.
     * @property name The name of the channel.
     * @property accessControl The access control settings of the channel.
     */
    data class Private(
        override val cId: UInt? = null,
        override val owner: UserInfo,
        override val name: ChannelName,
        override val accessControl: AccessControl,
        override val messages: List<Message> = emptyList(),
    ) : Channel

    companion object {
        /**
         * Creates a new channel.
         *
         * @param id The unique identifier of the channel.
         * @param owner The user that created the channel.
         * @param name The name of the channel.
         * @param accessControl The access control settings of the channel.
         * @param visibility The visibility of the channel. Must be either "PUBLIC" or "PRIVATE".
         * @return A new [Channel].
         */
        fun createChannel(
            id: UInt? = null,
            owner: UserInfo,
            name: ChannelName,
            accessControl: AccessControl,
            visibility: Visibility,
        ): Channel =
            when (visibility) {
                Visibility.PUBLIC -> Public(id, owner, name, accessControl)
                Visibility.PRIVATE -> Private(id, owner, name, accessControl)
            }
    }
}