package errors

/**
 * Represents the errors that can occur when handling users.
 */
abstract class UserError: Error() {
	data object UserNotFound : UserError()
	data object UserAlreadyExists : UserError()
}