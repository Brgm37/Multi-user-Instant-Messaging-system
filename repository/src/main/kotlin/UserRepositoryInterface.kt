import model.User

interface UserRepositoryInterface: Repository<User> {

    /**
     * Associates a user with a channel
     * @param uId The ID of the user
     * @param channelId The ID of the channel
     */
    fun joinChannel(uId: UInt, channelId: UInt)
}