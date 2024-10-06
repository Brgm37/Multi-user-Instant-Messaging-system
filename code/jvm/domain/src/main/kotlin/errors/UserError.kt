package errors



/**
 * Represents the errors that can occur when handling users.
 */
sealed class UserError: Error() {
	data object UserNotFound: UserError()
	data object UserAlreadyExists: UserError()
}