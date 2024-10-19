package errors

/**
 * Represents the errors that can occur when handling users.
 */
sealed class UserError : Error() {
    data object UserNotFound : UserError()

    data object UnableToCreateUser : UserError()

    data object UsernameIsEmpty : UserError()

    data object PasswordIsInvalid : UserError()

    data object InviterNotFound : UserError()

    data object InvitationCodeIsInvalid : UserError()

    data object InvitationCodeHasExpired : UserError()

    data object InvitationNotFound : UserError()

    data object UsernameAlreadyExists : UserError()

    data object UnableToCreateToken : UserError()

    data object TokenNotFound : UserError()

    data object UnableToCreateInvitation : UserError()

    data object InvitationCodeMaxUsesReached : UserError()

    data object UnableToDeleteToken : UserError()
}