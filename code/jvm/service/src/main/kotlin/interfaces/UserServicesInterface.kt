package interfaces

import errors.Error
import errors.UserError
import model.users.User
import model.users.UserInvitation
import model.users.UserToken
import utils.Either

/**
 * Represents the services available for the user entity.
 */
interface UserServicesInterface {
    /**
     * Creates a new user.
     * @param username The username of the user.
     * @param password The password of the user.
     * @param invitationCode The invitation code to join the app.
     * @param inviterUId The inviter user id.
     */
    fun createUser(
        username: String,
        password: String,
        invitationCode: String,
        inviterUId: UInt,
    ): Either<UserError, User>

    /**
     * Deletes a user.
     * @param id The id of the user to delete.
     */
    fun deleteUser(id: UInt): Either<UserError, Unit>

    /**
     * Gets a user by its id.
     * @param id The id of the user to get.
     */
    fun getUser(id: UInt): Either<UserError, User>

    /**
     * Associates a user to a channel.
     * @param userId The id of the user to join the channel.
     * @param channelId The id of the channel to join.
     * @param invitationCode The invitation code to join the channel.
     */
    fun joinChannel(
        userId: UInt,
        channelId: UInt,
        invitationCode: String,
    ): Either<Error, Unit>

    /**
     * Validates a token.
     * @param token The token to validate.
     */
    fun isValidToken(token: String): Either<UserError, Boolean>

    /**
     * Gets an invitation by inviterId and invitationCode
     * @param inviterUId inviter userId
     * @param invitationCode invitation code associated to the invitation
     */
    fun getInvitation(
        inviterUId: UInt,
        invitationCode: String,
    ): Either<UserError, UserInvitation>

    /**
     * Logs in a user.
     * @param username The username of the user.
     * @param password The password of the user.
     */
    fun login(
        username: String,
        password: String,
    ): Either<UserError, UserToken>
}