package errors

/**
 * Represents the errors that can occur when handling channels.
 */
sealed class ChannelError: Error() {
	data object UnableToCreateChannel: ChannelError()
	data object ChannelNotFound: ChannelError()
	data object OwnerNotFound: ChannelError()
	data object UnableToDeleteChannel: ChannelError()
	data object UnableToGetChannel: ChannelError()
	data object InvalidChannelInfo: ChannelError()
}