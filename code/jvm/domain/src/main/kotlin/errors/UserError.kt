package errors

/**
 * Represents the errors that can occur when handling users.
 */
abstract class UserError : Error() {
    data object UserNotFound : UserError()

    data object UserAlreadyExists : UserError()

    data object InvalidUserInfo : UserError()

    data object UsernameIsEmpty : UserError()

    data object PasswordIsInvalid : UserError()

    data object InvalidInviter : UserError()

    data object InviterNotFound : UserError()

    data object InvitationCodeIsInvalid : UserError()

    data object InvitationCodeHasExpired : UserError()
}