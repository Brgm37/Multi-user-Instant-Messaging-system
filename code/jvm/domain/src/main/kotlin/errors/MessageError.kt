package errors

sealed class MessageError : Error() {
    data object InvalidMessageInfo : MessageError()

    data object UserDoesNotHaveAccess : MessageError()

    data object UnableToCreateMessage : MessageError()

    data object UserNotInChannel : MessageError()

    data object UserNotFound : MessageError()

    data object ChannelNotFound : MessageError()

    data object MessageNotFound : MessageError()
}