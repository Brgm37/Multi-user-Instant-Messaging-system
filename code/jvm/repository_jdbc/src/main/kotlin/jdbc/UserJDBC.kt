package jdbc

import UserRepositoryInterface
import model.users.Password
import model.users.User
import model.users.UserInvitation
import model.users.UserToken
import utils.encryption.DummyEncrypt
import utils.encryption.Encrypt
import java.sql.Connection
import java.sql.ResultSet
import java.util.UUID

/**
 * Represent the maximum number of authentication tokens that a user can have.
 */

class UserJDBC(
    private val connection: Connection,
    private val encrypt: Encrypt = DummyEncrypt,
) : UserRepositoryInterface {
    companion object {
        const val MAX_TOKENS = 5
    }

    private fun ResultSet.toUser(): User =
        User(
            uId = getInt("id").toUInt(),
            username = getString("name"),
            password = Password(getString(("password"))),
        )

    private fun ResultSet.toUserInvitation(): UserInvitation =
        UserInvitation(
            userId = getInt("user_id").toUInt(),
            invitationCode = UUID.fromString(getString("invitation")),
            expirationDate = getTimestamp("expiration_date"),
        )

    override fun createUser(user: User): User? {
        val insertQuery =
            """
            INSERT INTO users (name, password)
            VALUES (?, ?) RETURNING id
            """.trimIndent()
        val stm = connection.prepareStatement(insertQuery)
        var idx = 1
        stm.setString(idx++, user.username)
        stm.setString(idx, (user.password.value))
        val rs = stm.executeQuery()
        return if (rs.next()) {
            user.copy(uId = rs.getInt("id").toUInt())
        } else {
            null
        }
    }

    override fun findInvitation(
        inviterUId: UInt,
        invitationCode: String,
    ): UserInvitation? {
        val selectQuery =
            """
            SELECT user_id, invitation, expiration_date
            FROM users_invitations
            WHERE user_id = ? AND invitation = ?
            """.trimIndent()
        val stm = connection.prepareStatement(selectQuery)
        stm.setInt(1, inviterUId.toInt())
        stm.setString(2, invitationCode)
        val rs = stm.executeQuery()
        return if (rs.next()) {
            rs.toUserInvitation()
        } else {
            null
        }
    }

    override fun deleteInvitation(invitation: UserInvitation) {
        val deleteQuery =
            """
            DELETE FROM users_invitations
            WHERE user_id = ? AND invitation = ?
            """.trimIndent()
        val stm = connection.prepareStatement(deleteQuery)
        stm.setInt(1, invitation.userId.toInt())
        stm.setString(2, invitation.invitationCode.toString())
        stm.executeUpdate()
    }

    override fun createInvitation(invitation: UserInvitation) {
        val insertQuery =
            """
            INSERT INTO users_invitations (user_id, invitation, expiration_date)
            VALUES (?, ?, ?)
            """.trimIndent()
        val stm = connection.prepareStatement(insertQuery)
        stm.setInt(1, invitation.userId.toInt())
        stm.setString(2, invitation.invitationCode.toString())
        stm.setTimestamp(3, invitation.expirationDate)
        stm.executeUpdate()
    }

    override fun validateToken(token: String): Boolean {
        val selectQuery =
            """
            SELECT user_id
            FROM users_tokens
            WHERE token = ?
            """.trimIndent()
        val stm = connection.prepareStatement(selectQuery)
        stm.setString(1, token)
        val rs = stm.executeQuery()
        return rs.next()
    }

    override fun findByToken(token: String): User? {
        val selectQuery =
            """
            SELECT user_id
            FROM users_tokens
            WHERE token = ?
            """.trimIndent()
        val stm = connection.prepareStatement(selectQuery)
        stm.setString(1, token)
        val rs = stm.executeQuery()
        return if (rs.next()) {
            findById(rs.getInt("user_id").toUInt())
        } else {
            null
        }
    }

    override fun findById(id: UInt): User? {
        val selectQuery =
            """
            SELECT id, name, password
            FROM users
            WHERE id = ?
            """.trimIndent()
        val stm = connection.prepareStatement(selectQuery)
        stm.setInt(1, id.toInt())
        val rs = stm.executeQuery()
        return if (rs.next()) {
            rs.toUser()
        } else {
            null
        }
    }

    override fun findByUsername(username: String): User? {
        val selectQuery =
            """
            SELECT id, name, password
            FROM users
            WHERE name = ?
            """.trimIndent()
        val stm = connection.prepareStatement(selectQuery)
        stm.setString(1, username)
        val rs = stm.executeQuery()
        return if (rs.next()) {
            rs.toUser()
        } else {
            null
        }
    }

    override fun createToken(token: UserToken): Boolean {
        val tokenCount = countUserTokens(token.userId)
        if (tokenCount >= MAX_TOKENS) {
            deleteOldestToken(token.userId)
        }
        val insertQuery =
            """
            INSERT INTO users_tokens (user_id, token, creation, expiration)
            VALUES (?, ?, ?, ?)
            """.trimIndent()
        val stm = connection.prepareStatement(insertQuery)
        var idx = 1
        stm.setInt(idx++, token.userId.toInt())
        stm.setString(idx++, token.token.toString())
        stm.setTimestamp(idx++, token.creationDate)
        stm.setTimestamp(idx, token.expirationDate)
        return stm.executeUpdate() > 0
    }

    private fun countUserTokens(userId: UInt): Int {
        val query = "SELECT COUNT(*) FROM users_tokens WHERE user_id = ?"
        val stm = connection.prepareStatement(query)
        stm.setInt(1, userId.toInt())
        val rs = stm.executeQuery()
        rs.next()
        return rs.getInt(1)
    }

    private fun deleteOldestToken(userId: UInt) {
        val deleteOldestQuery =
            """
            DELETE FROM users_tokens
            WHERE token IN (
                SELECT token
                FROM users_tokens
                WHERE user_id = ?
                ORDER BY creation 
                    FETCH 
                    FIRST 1 ROWS ONLY
            )
            """.trimIndent()
        val stmDelete = connection.prepareStatement(deleteOldestQuery)
        stmDelete.setInt(1, userId.toInt())
        stmDelete.executeUpdate()
    }

    override fun findAll(
        offset: Int,
        limit: Int,
    ): List<User> {
        val selectQuery =
            """
            SELECT id, name, password
            FROM users
            OFFSET ?
            LIMIT ?
            """.trimIndent()
        val stm = connection.prepareStatement(selectQuery)
        stm.setInt(1, offset)
        stm.setInt(2, limit)
        val rs = stm.executeQuery()
        val users = mutableListOf<User>()
        while (rs.next()) {
            users.add(rs.toUser())
        }
        return users
    }

    override fun save(entity: User) {
        val updateQuery =
            """
            UPDATE users
            SET name = ?, password = ?
            WHERE id = ?
            """.trimIndent()
        val stm = connection.prepareStatement(updateQuery)
        var idx = 1
        stm.setString(idx++, entity.username)
        stm.setString(idx++, entity.password.value)
        entity.uId?.let { stm.setInt(idx, it.toInt()) }
        stm.executeUpdate()
    }

    override fun deleteById(id: UInt) {
        executeDeleteQuery("DELETE FROM users_invitations WHERE user_id = ?", id)
        executeDeleteQuery("DELETE FROM users_tokens WHERE user_id = ?", id)
        executeDeleteQuery("DELETE FROM messages WHERE author = ?", id)
        executeDeleteQuery("DELETE FROM channel_members WHERE member = ?", id)
        executeDeleteQuery("DELETE FROM users WHERE id = ?", id)
    }

    private fun executeDeleteQuery(
        query: String,
        id: UInt,
    ) {
        val stm = connection.prepareStatement(query)
        stm.setInt(1, id.toInt())
        stm.executeUpdate()
    }

    @Suppress("SqlWithoutWhere")
    override fun clear() {
        val tables = listOf("users_tokens", "users_invitations", "users")

        for (table in tables) {
            val deleteQuery = "DELETE FROM $table"
            val stmDelete = connection.prepareStatement(deleteQuery)
            stmDelete.executeUpdate()
        }
    }
}