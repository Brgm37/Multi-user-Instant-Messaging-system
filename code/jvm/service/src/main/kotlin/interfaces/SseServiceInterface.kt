package interfaces

import errors.Error
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
}