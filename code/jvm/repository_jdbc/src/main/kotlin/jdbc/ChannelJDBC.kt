package jdbc

import ChannelRepositoryInterface
import model.*
import model.Visibility.PRIVATE
import model.Visibility.PUBLIC
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

/**
 * The name of the table in the database.
 * @property CHANNELS_TABLE The name of the table in the database.
 */
private const val CHANNELS_TABLE = "channels"

/**
 * The name of the column in the table [CHANNELS_TABLE].
 *
 * @property CHANNELS_TABLE_ID The name of the column in the table [CHANNELS_TABLE].
 */
private const val CHANNELS_TABLE_ID = "id"

/**
 * The name of the view in the database.
 *
 * @property CHANNELS_VIEW The name of the view in the database.
 */
private const val CHANNELS_VIEW = "v_channel"

/**
 * The name of the column in the view [CHANNELS_VIEW].
 *
 * @property CHANNELS_VIEW_ID The name of the column in the view [CHANNELS_VIEW].
 */
private const val CHANNELS_VIEW_ID = "channel_id"

/**
 * The name of the column in the view [CHANNELS_VIEW].
 *
 * @property CHANNELS_VIEW_NAME The name of the column in the view [CHANNELS_VIEW].
 */
private const val CHANNELS_VIEW_NAME = "channel_name"

/**
 * The name of the column in the view [CHANNELS_VIEW].
 *
 * @property CHANNELS_VIEW_OWNER The name of the column in the view [CHANNELS_VIEW].
 */
private const val CHANNELS_VIEW_OWNER = "channel_owner"

/**
 * The name of the column in the view [CHANNELS_VIEW].
 *
 * @property CHANNELS_VIEW_ACCESS_CONTROL The name of the column in the view [CHANNELS_VIEW].
 */
private const val CHANNELS_VIEW_ACCESS_CONTROL = "channel_accessControl"

/**
 * The name of the column in the view [CHANNELS_VIEW].
 *
 * @property CHANNELS_VIEW_VISIBILITY The name of the column in the view [CHANNELS_VIEW].
 */
private const val CHANNELS_VIEW_VISIBILITY = "channel_visibility"

/**
 * The name of the column in the view [CHANNELS_VIEW].
 *
 * @property CHANNELS_VIEW_OWNER_NAME The name of the column in the view [CHANNELS_VIEW].
 */
private const val CHANNELS_VIEW_OWNER_NAME = "owner_name"

/**
 * A JDBC implementation of the [ChannelRepositoryInterface].
 * @property connection The connection to the database.
 * @constructor Creates a [ChannelJDBC] with the given connection.
 */
class ChannelJDBC(
	private val connection: Connection
) : ChannelRepositoryInterface {
	/**
	 * Converts the [ResultSet] to a [Channel].
	 */
	private fun ResultSet.toChannel(): Channel {
		val id = getInt(CHANNELS_VIEW_ID).toUInt()
		val owner = UserInfo(
			uId = getInt(CHANNELS_VIEW_OWNER).toUInt(),
			username = getString(CHANNELS_VIEW_OWNER_NAME)
		)
		val name = getString(CHANNELS_VIEW_NAME).toChannelName()
		val accessControl = AccessControl.valueOf(getString(CHANNELS_VIEW_ACCESS_CONTROL).uppercase())
		val visibility = getString(CHANNELS_VIEW_VISIBILITY)
		return Channel.createChannel(
			id = id,
			owner = owner,
			name = name,
			accessControl = accessControl,
			visibility = Visibility.valueOf(visibility.uppercase())
		)
	}

	/**
	 * Converts the [ResultSet] to a list of [Channel].
	 */
	private fun ResultSet.toChannelList(): List<Channel> {
		val channels = mutableListOf<Channel>()
		while (next()) {
			channels.add(toChannel())
		}
		return channels
	}

	/**
	 * Sets the information of the [Channel] in the [PreparedStatement].
	 *
	 * @param channel The [Channel] to set the information.
	 */
	private fun PreparedStatement.setInfo(channel: Channel) {
		var idx = 1
		setInt(idx++, channel.owner.uId.toInt())
		setString(idx++, channel.name.fullName)
		setString(idx++, channel.accessControl.toString())
		when (channel) {
			is Channel.Public -> { setString(idx++, PUBLIC.name) }
			is Channel.Private -> { setString(idx++, PRIVATE.name) }
		}
		val id = channel.id
		if (id != null) {
			setInt(idx, id.toInt())
		}
	}

	override fun createChannel(channel: Channel): Channel {
		val insertQuery = """
                INSERT INTO channels (owner, name, accessControl, visibility)
                VALUES (?, ?, ?, ?) RETURNING id
            """.trimIndent()
		val stm = connection.prepareStatement(insertQuery)
		stm.setInfo(channel)
		val rs = stm.executeQuery()
		if (rs.next()) {
			return when (channel) {
				is Channel.Public -> channel.copy(id = rs.getInt(CHANNELS_TABLE_ID).toUInt())
				is Channel.Private -> channel.copy(id = rs.getInt(CHANNELS_TABLE_ID).toUInt())
			}
		} else {
			throw SQLException("Failed to create channel")
		}
	}

	override fun findByUserId(userId: UInt): List<Channel> {
		val selectQuery = """
				SELECT 
					channel_id, channel_name, channel_owner, channel_accessControl,
					channel_visibility, owner_name
				FROM v_channel
				WHERE channel_owner = ?
			""".trimIndent()
		val stm = connection.prepareStatement(selectQuery)
		stm.setInt(1, userId.toInt())
		val rs = stm.executeQuery()
		return rs.toChannelList()
	}

	override fun findById(id: UInt): Channel? {
		val selectQuery = """
				SELECT 
					channel_id, channel_name, channel_owner, channel_accessControl,
					channel_visibility, owner_name
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

	override fun findAll(): List<Channel> {
		val selectQuery = """
				SELECT 
					channel_id, channel_name, channel_owner, channel_accessControl,
					channel_visibility, owner_id, owner_name
				FROM v_channel
				WHERE channel_visibility = '${PUBLIC.name}'
			""".trimIndent()
		val stm = connection.prepareStatement(selectQuery)
		val rs = stm.executeQuery()
		return rs.toChannelList()
	}

	override fun save(entity: Channel) {
		val updateQuery = """
                UPDATE channels
                SET owner = ?, name = ?, accessControl = ?, visibility = ?
                WHERE id = ?
            """.trimIndent()
		val stm = connection.prepareStatement(updateQuery)
		stm.setInfo(entity)
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

	@Suppress("SqlWithoutWhere")
	override fun clear() {
		val deleteQuery = """
				DELETE FROM channels
			""".trimIndent()

		val deleteFromChannelMembersQuery = """
			DELETE FROM channel_members
		""".trimIndent()

		connection
			.prepareStatement(deleteFromChannelMembersQuery)
			.executeUpdate()
		connection
			.prepareStatement(deleteQuery)
			.executeUpdate()
	}
}
