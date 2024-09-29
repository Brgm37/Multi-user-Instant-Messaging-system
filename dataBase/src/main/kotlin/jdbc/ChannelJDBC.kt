package jdbc

import ChannelRepositoryInterface
import model.AccessControl
import model.Channel
import model.UserInfo
import model.toChannelName
import java.sql.SQLException

private const val PUBLIC_CHANNEL = "PUBLIC"
private const val PRIVATE_CHANNEL = "PRIVATE"

//TODO: Confirm it the concurrency is safe
class ChannelJDBC(
	envName: String
):ChannelRepositoryInterface, JDBC(envName) {
	override fun createChannel(channel: Channel): Channel {
		return connect { connection ->
			val insertQuery = """
				INSERT INTO channels (owner, name, accessControl, visibility)
				VALUES (?, ?, ?, ?) RETURNING id
			""".trimIndent()
			val stm = connection.prepareStatement(insertQuery)
			var idx = 1
			stm.setString(idx++, channel.owner.username)
			stm.setString(idx++, channel.name.name)
			stm.setString(idx++, channel.accessControl.toString())
			when(channel) {
				is Channel.Public -> { stm.setString(idx, PUBLIC_CHANNEL) }
				is Channel.Private -> { stm.setString(idx, PRIVATE_CHANNEL) }
			}
			val rs = stm.executeQuery()
			if(rs.next()) {
				return@connect when(channel) {
					is Channel.Public -> channel.copy(id = rs.getInt("id").toUInt())
					is Channel.Private -> channel.copy(id = rs.getInt("id").toUInt())
				}
			} else {
				throw SQLException("Failed to create channel")
			}
		}
	}

	override fun findById(id: UInt): Channel? {
		return connect { connection ->
			val selectChannelQuery = """
				SELECT owner, name, accessControl, visibility
				FROM channels
				WHERE id = ?
			""".trimIndent()
			val userInfoQuery = """
				SELECT name, id
				FROM users
				WHERE id = ?
			""".trimIndent()
			val channelStm = connection.prepareStatement(selectChannelQuery)
			channelStm.setInt(1, id.toInt())
			val channelRs = channelStm.executeQuery()
			if (channelRs.next()) {
				val ownerStm = connection.prepareStatement(userInfoQuery)
				ownerStm.setString(1, channelRs.getString("owner"))
				val ownerRs = ownerStm.executeQuery()
				if (ownerRs.next()) {
					val owner =
						UserInfo(
							uId = ownerRs.getInt("id").toUInt(),
							username = ownerRs.getString("name"),
						)
					return@connect when(channelRs.getString("visibility")) {
						PUBLIC_CHANNEL -> Channel.Public(
							id = id,
							owner = owner,
							name = channelRs.getString("name").toChannelName(),
							accessControl = AccessControl.valueOf(channelRs.getString("accessControl"))
						)
						PRIVATE_CHANNEL -> Channel.Private(
							id = id,
							owner = owner,
							name = channelRs.getString("name").toChannelName(),
							accessControl = AccessControl.valueOf(channelRs.getString("accessControl"))
						)
						else -> throw SQLException("Invalid channel visibility")
					}
				} else {
					throw SQLException("Owner not found")
				}
			} else {
				null
			}
		}
	}

	override fun findAll(): Sequence<Channel> {
		return connect { connection ->
			val selectQuery = """
				SELECT id, owner, name, accessControl, visibility
				FROM channels
			""".trimIndent()
			val userInfoQuery = """
				SELECT name, id
				FROM users
				WHERE id = ?
			""".trimIndent()
			val stm = connection.prepareStatement(selectQuery)
			val rs = stm.executeQuery()
			sequence {
				while (rs.next()) {
					val ownerStm = connection.prepareStatement(userInfoQuery)
					ownerStm.setString(1, rs.getString("owner"))
					val ownerRs = ownerStm.executeQuery()
					if (ownerRs.next()) {
						val owner =
							UserInfo(
								uId = ownerRs.getInt("id").toUInt(),
								username = ownerRs.getString("name"),
							)
						val channel = when (rs.getString("visibility")) {
							PUBLIC_CHANNEL -> Channel.Public(
								id = rs.getInt("id").toUInt(),
								owner = owner,
								name = rs.getString("name").toChannelName(),
								accessControl = AccessControl.valueOf(rs.getString("accessControl"))
							)

							PRIVATE_CHANNEL -> Channel.Private(
								id = rs.getInt("id").toUInt(),
								owner = owner,
								name = rs.getString("name").toChannelName(),
								accessControl = AccessControl.valueOf(rs.getString("accessControl"))
							)

							else -> throw SQLException("Invalid channel visibility")
						}
						yield(channel)
					} else {
						throw SQLException("Owner not found")
					}
				}
			}
		}
	}

	override fun save(entity: Channel) {
		connect { connection ->
			val updateQuery = """
				UPDATE channels
				SET owner = ?, name = ?, accessControl = ?, visibility = ?
				WHERE id = ?
			""".trimIndent()
			val stm = connection.prepareStatement(updateQuery)
			var idx = 1
			stm.setString(idx++, entity.owner.username)
			stm.setString(idx++, entity.name.name)
			stm.setString(idx++, entity.accessControl.toString())
			when(entity) {
				is Channel.Public -> { stm.setString(idx++, PUBLIC_CHANNEL) }
				is Channel.Private -> { stm.setString(idx++, PRIVATE_CHANNEL) }
			}
			val id = requireNotNull(entity.id) { "Channel id is null" }
			stm.setInt(idx, id.toInt())
			stm.executeUpdate()
		}
	}

	override fun deleteById(id: UInt) {
		connect { connection ->
			val deleteQuery = """
				DELETE FROM channels
				WHERE id = ?
			""".trimIndent()
			val stm = connection.prepareStatement(deleteQuery)
			stm.setInt(1, id.toInt())
			stm.executeUpdate()
		}
	}
}
