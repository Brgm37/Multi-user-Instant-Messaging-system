import model.User

interface UserRepositoryInterface: Repository<User> {
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
    fun joinChannel(uId: UInt, channelId: UInt)
}