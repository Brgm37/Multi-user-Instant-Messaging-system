package jdbc

import ChannelRepositoryInterface
import model.AccessControl
import model.Channel
import model.ChannelInvitation
import model.UserInfo
import model.Visibility
import model.Visibility.PRIVATE
import model.Visibility.PUBLIC
import model.toChannelName
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
 * @property CHANNELS_VIEW_INVITATION The name of the column in the view [CHANNELS_VIEW].
 */
private const val CHANNELS_VIEW_INVITATION = "channel_invitation"

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
	private val connection: Connection,
) : ChannelRepositoryInterface {
	/**
	 * Converts the [ResultSet] to a [Channel].
	 */
	private fun ResultSet.toChannel(): Channel {
		val id = getInt(CHANNELS_VIEW_ID).toUInt()
		val owner =
			UserInfo(
				uId = getInt(CHANNELS_VIEW_OWNER).toUInt(),
				username = getString(CHANNELS_VIEW_OWNER_NAME),
			)
		val name = getString(CHANNELS_VIEW_NAME).toChannelName()
		val accessControl = AccessControl.valueOf(getString(CHANNELS_VIEW_ACCESS_CONTROL).uppercase())
		val visibility = getString(CHANNELS_VIEW_VISIBILITY)
		val channel =
			Channel.createChannel(
				id = id,
				owner = owner,
				name = name,
				accessControl = accessControl,
				visibility = Visibility.valueOf(visibility.uppercase()),
			)
		return channel
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
	 * @sample
	 */
	private fun PreparedStatement.setInfo(channel: Channel) {
		var idx = 1
		setInt(idx++, channel.owner.uId.toInt())
		setString(idx++, channel.name.fullName)
		setString(idx++, channel.accessControl.toString())
		when (channel) {
			is Channel.Public -> {
				setString(idx++, PUBLIC.name)
			}

			is Channel.Private -> {
				setString(idx++, PRIVATE.name)
			}
		}
		val id = channel.channelId
		if (id != null) {
			setInt(idx, id.toInt())
		}
	}

	override fun createChannel(channel: Channel): Channel {
		val insertQuery =
			"""
			INSERT INTO channels (owner, name, accessControl, visibility)
			VALUES (?, ?, ?, ?) RETURNING id
			""".trimIndent()
		val stm = connection.prepareStatement(insertQuery)
		stm.setInfo(channel)
		val rs = stm.executeQuery()
		if (rs.next()) {
			return when (channel) {
				is Channel.Public -> channel.copy(channelId = rs.getInt(CHANNELS_TABLE_ID).toUInt())
				is Channel.Private -> channel.copy(channelId = rs.getInt(CHANNELS_TABLE_ID).toUInt())
			}
		} else {
			throw SQLException("Failed to create channel")
		}
	}

	override fun findByUserId(
		userId: UInt,
		offset: Int,
		limit: Int,
	): List<Channel> {
		val selectQuery =
			"""
			SELECT 
				channel_id, channel_name, channel_owner, channel_accessControl, channel_invitation,
				channel_visibility, owner_name
			FROM v_channel
			WHERE channel_owner = ?
			LIMIT ?
			OFFSET ?
			""".trimIndent()
		val stm = connection.prepareStatement(selectQuery)
		var idx = 1
		stm.setInt(idx++, userId.toInt())
		stm.setInt(idx++, limit)
		stm.setInt(idx, offset)
		val rs = stm.executeQuery()
		return rs.toChannelList()
	}

	override fun joinChannel(
		channelId: UInt,
		userId: UInt,
	) {
		val insertQuery =
			"""
			INSERT INTO channel_members (channel, member)
			VALUES (?, ?)
			""".trimIndent()
		val stm = connection.prepareStatement(insertQuery)
		stm.setInt(1, channelId.toInt())
		stm.setInt(2, userId.toInt())
		stm.executeUpdate()
	}

	override fun isUserInChannel(
		channelId: UInt,
		userId: UInt,
	): Boolean {
		TODO("Not yet implemented")
	}

	override fun findInvitation(channelId: UInt): ChannelInvitation? {
		TODO("Not yet implemented")
	}

	override fun updateInvitation(
		channelId: UInt,
		invitation: ChannelInvitation,
	) {
		TODO("Not yet implemented")
	}

	override fun deleteInvitation(channelId: UInt) {
		TODO("Not yet implemented")
	}

	override fun createInvitation(invitation: ChannelInvitation) {
		TODO("Not yet implemented")
	}

	override fun findById(id: UInt): Channel? {
		val selectQuery =
			"""
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

	override fun findAll(
		offset: Int,
		limit: Int,
	): List<Channel> {
		val selectQuery =
			"""
			SELECT 
				channel_id, channel_name, channel_owner, channel_accessControl,
				channel_visibility, owner_id, owner_name
			FROM v_channel
			WHERE channel_visibility = '${PUBLIC.name}'
			LIMIT ?
			OFFSET ?
			""".trimIndent()
		val stm = connection.prepareStatement(selectQuery)
		var idx = 1
		stm.setInt(idx++, limit)
		stm.setInt(idx, offset)
		val rs = stm.executeQuery()
		return rs.toChannelList()
	}

	override fun save(entity: Channel) {
		val updateQuery =
			"""
			UPDATE channels
			SET owner = ?, name = ?, accessControl = ?, visibility = ?
			WHERE id = ?
			""".trimIndent()
		val stm = connection.prepareStatement(updateQuery)
		stm.setInfo(entity)
		stm.executeUpdate()
	}

	override fun deleteById(id: UInt) {
		val deleteQuery =
			"""
			DELETE FROM channels
			WHERE id = ?
			""".trimIndent()
		val stm = connection.prepareStatement(deleteQuery)
		stm.setInt(1, id.toInt())
		stm.executeUpdate()
	}

	@Suppress("SqlWithoutWhere")
	override fun clear() {
		val deleteQuery =
			"""
			DELETE FROM channels
			""".trimIndent()

		val deleteFromChannelMembersQuery =
			"""
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
