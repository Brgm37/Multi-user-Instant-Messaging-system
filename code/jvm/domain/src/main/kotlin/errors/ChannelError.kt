package errors

/**
 * Represents the errors that can occur when handling channels.
 */
sealed class ChannelError : Error() {
    data object UnableToCreateChannel : ChannelError()

    data object ChannelNotFound : ChannelError()

    data object UserNotFound : ChannelError()

    data object OwnerNotFound : ChannelError()

    data object UnableToDeleteChannel : ChannelError()

    data object UnableToGetChannel : ChannelError()

    data object InvalidChannelInfo : ChannelError()

    data object InvalidChannelVisibility : ChannelError()

    data object InvalidChannelAccessControl : ChannelError()

    data object UnableToJoinChannel : ChannelError()

    data object InvitationCodeHasExpired : UserError()

    data object InvitationCodeMaxUsesReached : UserError()

    data object InvitationCodeIsInvalid : UserError()
}
