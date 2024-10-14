package errors

import utils.failure

sealed class MessageError : Error() {

    data object EmptyMessageContent : MessageError()

    data object UserNotInChannel : MessageError()

    data object UnableToCreateMessage : MessageError()

    data object UserHasNoWriteAccess : MessageError()

    data object UserNotFound : MessageError()

    data object ChannelNotFound : MessageError()

    data object MessageNotFound : MessageError()
}
