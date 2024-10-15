package jdbc

import UserRepositoryInterface
import model.users.Password
import model.users.User
import model.users.UserInvitation
import java.sql.Connection
import java.sql.ResultSet
import java.util.UUID

class UserJDBC(
    private val connection: Connection,
) : UserRepositoryInterface {
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
            SELECT id, name, password, token
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

    override fun findAll(
        offset: Int,
        limit: Int,
    ): List<User> {
        val selectQuery =
            """
            SELECT id, name, password
            FROM users
            """.trimIndent()
        val stm = connection.prepareStatement(selectQuery)
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
        val deleteFromUserChannelsQuery =
            """
            DELETE FROM channel_members
            WHERE member = ?
            """.trimIndent()
        val deleteFromUsersQuery =
            """
            DELETE FROM users
            WHERE id = ?
            """.trimIndent()

        val stmUserChannels = connection.prepareStatement(deleteFromUserChannelsQuery)
        stmUserChannels.setInt(1, id.toInt())
        stmUserChannels.executeUpdate()

        val stmUsers = connection.prepareStatement(deleteFromUsersQuery)
        stmUsers.setInt(1, id.toInt())
        stmUsers.executeUpdate()
    }

    @Suppress("SqlWithoutWhere")
    override fun clear() {
        val deleteFromUsersQuery =
            """
            DELETE FROM users
            """.trimIndent()
        val deleteFromUsersInvitationsQuery =
            """
            DELETE FROM users_invitations
            """.trimIndent()
        val stmDeleteInvitations = connection.prepareStatement(deleteFromUsersInvitationsQuery)
        stmDeleteInvitations.executeUpdate()

        val stmDelete = connection.prepareStatement(deleteFromUsersQuery)
        stmDelete.executeUpdate()
    }
}