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
     * Retrieves an invitation by its code
     * @param invitationCode The invitation code
     */
    fun findInvitation(invitationCode: String): UserInvitation?

    /**
     * Deletes an invitation associated to the user
     * @param invitation The invitation to delete
     */
    fun deleteInvitation(invitation: UserInvitation)

    /**
     * Creates an invitation for the user
     * @param invitation The invitation to create
     */
    fun createInvitation(invitation: UserInvitation): Boolean

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

    /**
     * Deletes the token associated to the user
     * @param token The token to delete
     */
    fun deleteToken(token: String): Boolean

    /**
     * Retrieves a token by its value
     *
     * @param token The token to find
     * @return The token if found, null otherwise
     */
    fun findToken(token: String): UserToken?

    /**
     * Retrieves a user by its ID
     *
     * @param token The ID of the user
     * @return The user if found, null otherwise
     */
    fun findByToken(token: String): User?
}