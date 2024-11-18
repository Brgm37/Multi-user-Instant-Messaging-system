package interfaces

import errors.UserError
import model.users.User
import utils.Either

/**
 * Represents the services available for the authentication.
 */
interface AuthServiceInterface {
    /**
     * Validates a token.
     * @param token The token to validate.
     */
    fun getUserByToken(token: String): Either<UserError, User>
}