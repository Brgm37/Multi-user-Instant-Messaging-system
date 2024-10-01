package errors

/**
 * Represents the errors that can occur when handling channels.
 */
abstract class ChannelError: Error() {
	data object UnableToCreateChannel: ChannelError()
	data object ChannelNotFound: ChannelError()
	data object UnableToDeleteChannel: ChannelError()
	data object UnableToGetChannel: ChannelError()
}