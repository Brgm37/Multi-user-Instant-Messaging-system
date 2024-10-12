package interfaces

import errors.Error
import errors.UserError
import model.User
import utils.Either

/**
 * Represents the services available for the user entity.
 */
interface UserServicesInterface {
	/**
	 * Creates a new user.
	 * @param user The user info to create.
	 * @return The created [User].
	 */
	fun createUser(user: User): Either<UserError, User>

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
	 */
	fun joinChannel(
		userId: UInt,
		channelId: UInt,
	): Either<Error, Unit>
}
