import model.User
import model.UserInvitation

interface UserRepositoryInterface : Repository<User> {
	/**
	 * Creates a new user and retrieves its ID
	 * @param user The user to create
	 * @return The created user
	 */
	fun createUser(user: User): User?

	/**
	 * Associates a user with a channel
	 * @param uId The ID of the user
	 * @param channelId The ID of the channel
	 */
	fun joinChannel(
		uId: UInt,
		channelId: UInt,
	)

	/**
	 * Retrieves an invitation associated to the user
	 * @param inviterUId The ID of the inviter
	 * @param invitationCode The invitation code
	 */
	fun findInvitation(
		inviterUId: UInt,
		invitationCode: String,
	): UserInvitation?

	/**
	 * Deletes an invitation associated to the user
	 * @param invitation The invitation to delete
	 */
	fun deleteInvitation(invitation: UserInvitation)

	/**
	 * Creates an invitation for the user
	 * @param invitation The invitation to create
	 */
	fun createInvitation(invitation: UserInvitation)
}
