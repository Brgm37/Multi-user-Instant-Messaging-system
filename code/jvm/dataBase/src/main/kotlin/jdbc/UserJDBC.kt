package jdbc

import UserRepositoryInterface
import model.User
import org.eclipse.jetty.util.security.Password
import org.eclipse.jetty.util.security.Password.obfuscate
import java.sql.ResultSet
import java.util.*

class UserJDBC(
	envName: String
): UserRepositoryInterface, JDBC(envName){

	private fun ResultSet.toUser(): User {
		return User(
			uId = getInt("id").toUInt(),
			username = getString("username"),
			password = Password(obfuscate(getString("password"))),
			token = UUID.fromString(getString("token")),
		)
	}
	override fun createUser(user: User): User? {
		return connect { connection ->
			val insertQuery = """
				INSERT INTO users (username, password)
				VALUES (?, ?) RETURNING id
			""".trimIndent()
			val stm = connection.prepareStatement(insertQuery)
			var idx = 1
			stm.setString(idx++, user.username)
			stm.setString(idx++, obfuscate(user.password.toString()))
			val rs = stm.executeQuery()
			if (rs.next()) {
				return@connect user.copy(uId = rs.getInt("id").toUInt())
			} else {
				null
			}

		}
	}

	override fun joinChannel(uId: UInt, channelId: UInt) {
		return connect { connection ->
			val insertQuery = """
				INSERT INTO user_channels (user_id, channel_id)
				VALUES (?, ?)
			""".trimIndent()
			val stm = connection.prepareStatement(insertQuery)
			var idx = 1
			stm.setInt(idx++, uId.toInt())
			stm.setInt(idx++, channelId.toInt())
			stm.executeUpdate()
		}
	}

	override fun findById(id: UInt): User? {
		return connect { connection ->
			val selectQuery = """
				SELECT id, username, password, token
				FROM users
				WHERE id = ?
			""".trimIndent()
			val stm = connection.prepareStatement(selectQuery)
			stm.setInt(1, id.toInt())
			val rs = stm.executeQuery()
			if (rs.next()) {
				return@connect rs.toUser()
			} else {
				null
			}
		}
	}

	override fun findAll(): Sequence<User> {
		return connect { connection ->
			val selectQuery = """
				SELECT id, username, password, token
				FROM users
			""".trimIndent()
			val stm = connection.prepareStatement(selectQuery)
			val rs = stm.executeQuery()
			sequence {
				while (rs.next()) {
					yield(rs.toUser())
				}
			}
		}
	}

	override fun save(entity: User) {
		return connect { connection ->
			val updateQuery = """
				UPDATE users
				SET username = ?, password = ?
				WHERE id = ?
			""".trimIndent()
			val stm = connection.prepareStatement(updateQuery)
			var idx = 1
			stm.setString(idx++, entity.username)
			stm.setString(idx++, obfuscate(entity.password.toString()))
			entity.uId?.let { stm.setInt(idx, it.toInt()) }
			stm.executeUpdate()
		}
	}

	override fun deleteById(id: UInt) {
		return connect { connection ->
			val deleteFromUserChannelsQuery = """
            DELETE FROM user_channels
            WHERE user_id = ?
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

}