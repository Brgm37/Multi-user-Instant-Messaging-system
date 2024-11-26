package interfaces

import errors.Error
import model.messages.Message
import utils.Either

/**
 * Represents the service that provides the operations related to the sse.
 */
interface SseServiceInterface {
    /**
     * Verifies if the user is in the channel.
     *
     * @param cId The channel id
     * @param uId The user id
     *
     * @return Either<Error, Boolean> The result of the operation
     */
    fun isUserInChannel(
        cId: UInt,
        uId: UInt,
    ): Either<Error, Boolean>

    /**
     * Emits all messages to the user.
     *
     * @param uId The user ID
     * @param lastEventId The last event ID
     * @param emitter The emitter function
     */
    fun emitAllMessages(
        uId: UInt,
        lastEventId: UInt,
        emitter: (Message) -> Unit,
    )
}