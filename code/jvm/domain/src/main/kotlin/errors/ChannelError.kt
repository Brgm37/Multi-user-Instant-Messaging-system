package errors

/**
 * Represents the errors that can occur when handling channels.
 */
sealed class ChannelError : Error() {
    data object UnableToCreateChannel : ChannelError()

    data object ChannelNotFound : ChannelError()

    data object UserNotFound : ChannelError()

    data object InvalidChannelInfo : ChannelError()

    data object InvalidChannelVisibility : ChannelError()

    data object InvalidChannelAccessControl : ChannelError()
}