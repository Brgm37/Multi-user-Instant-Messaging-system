package model

/**
 * A channel is a container for user messages.
 *
 * @property id The unique identifier of the channel.
 * @property owner The user that created the channel.
 * @property name The name of the channel.
 * @property accessControl The access control settings of the channel.
 */
sealed class Channel {
    abstract val id: UInt?
    abstract val owner: UserInfo
    abstract val name: ChannelName
    abstract val accessControl: AccessControl

    /**
     * A public channel is visible to all users.
     *
     * @property id The unique identifier of the channel.
     * @property owner The user that created the channel.
     * @property name The name of the channel.
     * @property accessControl The access control settings of the channel.
     */
    data class Public(
        override val id: UInt? = null,
        override val owner: UserInfo,
        override val name: ChannelName,
        override val accessControl: AccessControl,
    ) : Channel()

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
        override val id: UInt? = null,
        override val owner: UserInfo,
        override val name: ChannelName,
        override val accessControl: AccessControl,
    ) : Channel()

	companion object {
		/**
		 * Creates a new channel.
		 *
		 * @param id The unique identifier of the channel.
		 * @param owner The user that created the channel.
		 * @param name The name of the channel.
		 * @param accessControl The access control settings of the channel.
		 * @param visibility The visibility of the channel. Must be either "PUBLIC" or "PRIVATE".
		 * @return A new channel.
		 */
		fun createChannel(
			id: UInt? = null,
			owner: UserInfo,
			name: ChannelName,
			accessControl: AccessControl,
			visibility: Visibility
		): Channel {
			return when (visibility) {
				Visibility.PUBLIC -> Public(id, owner, name, accessControl)
				Visibility.PRIVATE -> Private(id, owner, name, accessControl)
			}
		}
	}
}
