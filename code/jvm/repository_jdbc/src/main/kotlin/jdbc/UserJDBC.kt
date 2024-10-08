package jdbc

import UserRepositoryInterface
import model.Password
import model.User
import java.sql.Connection
import java.sql.ResultSet
import java.util.*

class UserJDBC(
	private val connection: Connection
): UserRepositoryInterface{

	private fun ResultSet.toUser(): User {
		return User(
			uId = getInt("id").toUInt(),
			username = getString("username"),
			password = Password(getString(("password"))),
			token = UUID.fromString(getString("token")),
		)
	}
	override fun createUser(user: User): User? {
		val insertQuery = """
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

	override fun joinChannel(uId: UInt, channelId: UInt) {
		val insertQuery = """
				INSERT INTO channel_members (member, channel)
				VALUES (?, ?)
			""".trimIndent()
		val stm = connection.prepareStatement(insertQuery)
		var idx = 1
		stm.setInt(idx++, uId.toInt())
		stm.setInt(idx, channelId.toInt())
		stm.executeUpdate()
	}

	override fun findById(id: UInt): User? {
		val selectQuery = """
				SELECT id, name, password, token
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

	override fun findAll(): Sequence<User> {
		val selectQuery = """
				SELECT id, name, password, token
				FROM users
			""".trimIndent()
		val stm = connection.prepareStatement(selectQuery)
		val rs = stm.executeQuery()
		return sequence {
			while (rs.next()) {
				yield(rs.toUser())
			}
		}
	}

	override fun save(entity: User) {
		val updateQuery = """
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
		val deleteFromUserChannelsQuery = """
            DELETE FROM channel_members
            WHERE member = ?
        """.trimIndent()
		val deleteFromUsersQuery = """
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
}
