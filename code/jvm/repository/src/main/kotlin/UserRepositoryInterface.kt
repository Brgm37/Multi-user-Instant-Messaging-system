import model.users.User
import model.users.UserInvitation
import model.users.UserToken

interface UserRepositoryInterface : Repository<User> {
    /**
     * Creates a new user and retrieves its ID
     * @param user The user to create
     * @return The created user
     */
    fun createUser(user: User): User?

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

    /**
     * Validates if a token exists and is not expired
     *
     * @param token The token to validate
     */
    fun validateToken(token: String): Boolean

    /**
     * Retrieves a User by its username
     * @param username the username of the user
     */
    fun findByUsername(username: String): User?

    /**
     * Creates a new authentication token for the user
     * @param token The token to create
     */
    fun createToken(token: UserToken): Boolean
}