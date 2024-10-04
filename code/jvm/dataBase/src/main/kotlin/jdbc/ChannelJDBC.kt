package jdbc

import ChannelRepositoryInterface
import jakarta.inject.Named
import model.*
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException

private const val PUBLIC_CHANNEL = "PUBLIC"
private const val PRIVATE_CHANNEL = "PRIVATE"

/**
 * A JDBC implementation of the [ChannelRepositoryInterface].
 * @property connection The connection to the database.
 * @constructor Creates a [ChannelJDBC] with the given connection.
 */
@Named("ChannelJDBC")
class ChannelJDBC(
	private val connection: Connection
) : ChannelRepositoryInterface {

	private fun ResultSet.toChannel(): Channel {
		val id = getInt("channel_id").toUInt()
		val owner = UserInfo(
			uId = getInt("owner_id").toUInt(),
			username = getString("owner_name")
		)
		val name = getString("channel_name").toChannelName()
		val accessControl = AccessControl.valueOf(getString("channel_accessControl"))
		val visibility = getString("channel_visibility")
		return Channel.createChannel(
			id = id,
			owner = owner,
			name = name,
			accessControl = accessControl,
			visibility = Visibility.valueOf(visibility.uppercase())
		)
	}

	override fun createChannel(channel: Channel): Channel {
		val insertQuery = """
                INSERT INTO channels (owner, name, accessControl, visibility)
                VALUES (?, ?, ?, ?) RETURNING id
            """.trimIndent()
		val stm = connection.prepareStatement(insertQuery)
		var idx = 1
		stm.setString(idx++, channel.owner.username)
		stm.setString(idx++, channel.name.fullName)
		stm.setString(idx++, channel.accessControl.toString())
		when (channel) {
			is Channel.Public -> { stm.setString(idx, PUBLIC_CHANNEL) }
			is Channel.Private -> { stm.setString(idx, PRIVATE_CHANNEL) }
		}
		val rs = stm.executeQuery()
		if (rs.next()) {
			return when (channel) {
				is Channel.Public -> channel.copy(id = rs.getInt("id").toUInt())
				is Channel.Private -> channel.copy(id = rs.getInt("id").toUInt())
			}
		} else {
			throw SQLException("Failed to create channel")
		}
	}

	override fun findById(id: UInt): Channel? {
		val selectQuery = """
				SELECT 
					channel_id, channel_name, channel_owner, channel_accessControl,
					channel_visibility, owner_id, owner_name
				FROM v_channel
				WHERE channel_id = ?
			""".trimIndent()
		val stm = connection.prepareStatement(selectQuery)
		stm.setInt(1, id.toInt())
		val rs = stm.executeQuery()
		return if (rs.next()) {
			rs.toChannel()
		} else {
			null
		}
	}

	override fun findAll(): Sequence<Channel> {
		val selectQuery = """
				SELECT 
					channel_id, channel_name, channel_owner, channel_accessControl,
					channel_visibility, owner_id, owner_name
				FROM v_channel
				WHERE channel_visibility = '$PUBLIC_CHANNEL'
			""".trimIndent()
		val stm = connection.prepareStatement(selectQuery)
		val rs = stm.executeQuery()
		return sequence {
			while (rs.next()) {
				yield(rs.toChannel())
			}
		}
	}

	override fun save(entity: Channel) {
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
		when (entity) {
			is Channel.Public -> { stm.setString(idx++, PUBLIC_CHANNEL) }
			is Channel.Private -> { stm.setString(idx++, PRIVATE_CHANNEL) }
		}
		val id = requireNotNull(entity.id) { "Channel id is null" }
		stm.setInt(idx, id.toInt())
		stm.executeUpdate()
	}

	override fun deleteById(id: UInt) {
		val deleteQuery = """
                DELETE FROM channels
                WHERE id = ?
            """.trimIndent()
		val stm = connection.prepareStatement(deleteQuery)
		stm.setInt(1, id.toInt())
		stm.executeUpdate()
	}
}
