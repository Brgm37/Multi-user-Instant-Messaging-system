package mem

import UserRepositoryInterface
import model.users.User
import model.users.UserInvitation
import model.users.UserToken
import java.util.UUID

class UserInMem : UserRepositoryInterface {
    private val users = mutableListOf<User>()
    private val invitations = mutableListOf<UserInvitation>()
    private val tokens = mutableListOf<UserToken>()
    private var nextId = 1u

    override fun createUser(user: User): User =
        user
            .copy(uId = nextId++)
            .also { users.add(it) }

    override fun findInvitation(
        inviterUId: UInt,
        invitationCode: String,
    ): UserInvitation? = invitations.find { it.userId == inviterUId && it.invitationCode.toString() == invitationCode }

    override fun deleteInvitation(invitation: UserInvitation) {
        invitations.removeIf { it.invitationCode == invitation.invitationCode }
    }

    override fun createInvitation(invitation: UserInvitation) {
        invitations.add(invitation)
    }

    override fun validateToken(token: String): Boolean {
        val tokenObj = tokens.find { it.token == UUID.fromString(token) } ?: return false
        return !tokenObj.isExpired()
    }

    override fun findByUsername(username: String): User? = users.find { it.username == username }

    override fun createToken(token: UserToken): Boolean {
        tokens.add(token)
        return true
    }

    override fun findByToken(token: String): User? {
        val tokenObj =
            try {
                UUID.fromString(token)
            } catch (e: Exception) {
                return null
            }
        return tokens
            .find { it.token == tokenObj }
            ?.let { findById(it.userId) }
    }

    override fun findById(id: UInt): User? = users.find { it.uId == id }

    override fun findAll(
        offset: Int,
        limit: Int,
    ): List<User> = users.drop(offset).take(limit)

    override fun save(entity: User) {
        users.removeIf { it.uId == entity.uId }
        users.add(entity)
    }

    override fun deleteById(id: UInt) {
        users.removeIf { it.uId == id }
    }

    override fun clear() {
        users.clear()
        invitations.clear()
    }
}