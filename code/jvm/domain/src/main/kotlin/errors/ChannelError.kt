package errors

abstract class ChannelError: Error() {
	data object ChannelNotFound : ChannelError()
}