package mem

import UserRepositoryInterface
import model.users.User
import model.users.UserInvitation

class UserInMem : UserRepositoryInterface {
    private val users = mutableListOf<User>()
    private var nextId = 1u

    override fun createUser(user: User): User =
        user
            .copy(uId = nextId++)
            .also { users.add(it) }

    override fun findInvitation(
        inviterUId: UInt,
        invitationCode: String,
    ): UserInvitation? {
        TODO("Not yet implemented")
    }

    override fun deleteInvitation(invitation: UserInvitation) {
        TODO("Not yet implemented")
    }

    override fun createInvitation(invitation: UserInvitation) {
        TODO("Not yet implemented")
    }

    override fun validateToken(token: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun findById(id: UInt): User? = users.find { it.uId == id }

    override fun findAll(
        offset: Int,
        limit: Int,
    ): List<User> {
        TODO("Not yet implemented")
    }

    override fun save(entity: User) {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: UInt) {
        TODO("Not yet implemented")
    }

    override fun clear() {
        // TODO: Implement this method
        return
    }
}