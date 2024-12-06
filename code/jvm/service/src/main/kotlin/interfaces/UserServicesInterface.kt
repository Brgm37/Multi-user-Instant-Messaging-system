package interfaces

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
     */
    fun createUser(
        username: String,
        password: String,
        invitationCode: String,
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
     * Gets an invitation by inviterId and invitationCode
     * @param invitationCode invitation code associated to the invitation
     */
    fun getInvitation(invitationCode: String): Either<UserError, UserInvitation>

    /**
     * Logs in a user.
     * @param username The username of the user.
     * @param password The password of the user.
     */
    fun login(
        username: String,
        password: String,
    ): Either<UserError, UserToken>

    /**
     * Logs out a user.
     * @param token The authentication token of the user logging out.
     * @param uId The id of the user logging out.
     */
    fun logout(
        token: String,
        uId: UInt,
    ): Either<UserError, Unit>

    /**
     * Creates an invitation.
     * @param inviterUId The id of the inviter.
     */
    fun createInvitation(inviterUId: UInt): Either<UserError, UserInvitation>
}