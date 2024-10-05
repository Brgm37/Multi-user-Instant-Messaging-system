package errors

/**
 * Represents the errors that can occur when handling events.
 */
sealed class Error {
	data object MessageNotFound : Error()
	data object UserNotFound : Error()
	data object UserAlreadyExists : Error()
	data object ChannelNotFound : Error()
}