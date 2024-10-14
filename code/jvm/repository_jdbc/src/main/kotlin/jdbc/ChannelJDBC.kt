package jdbc

import ChannelRepositoryInterface
import model.channels.AccessControl
import model.channels.Channel
import model.channels.Channel.Companion.createChannel
import model.channels.ChannelInvitation
import model.channels.Visibility
import model.channels.Visibility.PRIVATE
import model.channels.Visibility.PUBLIC
import model.channels.toChannelName
import model.users.UserInfo
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.UUID

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
 * The name of the table in the database.
 *
 * @property CHANNELS_INVITATIONS_TABLE The name of the table in the database.
 */
private const val CHANNELS_INVITATIONS_TABLE = "channels_invitations"

/**
 * The name of the table in the database.
 *
 * @property CHANNELS_INVITATIONS_TABLE The name of the table in the database.
 */
private const val CHANNELS_INVITATIONS_CHANNEL_ID = "channel_id"

/**
 * The name of the column in the table [CHANNELS_INVITATIONS_TABLE].
 *
 * @property CHANNELS_INVITATIONS_EXPIRATION_DATE The name of the column in the table [CHANNELS_INVITATIONS_TABLE].
 */
private const val CHANNELS_INVITATIONS_EXPIRATION_DATE = "expirationDate"

/**
 * The name of the column in the table [CHANNELS_INVITATIONS_TABLE].
 *
 * @property CHANNELS_INVITATIONS_INVITATION The name of the column in the table [CHANNELS_INVITATIONS_TABLE].
 */
private const val CHANNELS_INVITATIONS_INVITATION = "invitation"

/**
 * The name of the column in the table [CHANNELS_INVITATIONS_TABLE].
 *
 * @property CHANNELS_INVITATIONS_ACCESS_CONTROL The name of the column in the table [CHANNELS_INVITATIONS_TABLE].
 */
private const val CHANNELS_INVITATIONS_ACCESS_CONTROL = "accessControl"

/**
 * The name of the column in the table [CHANNELS_INVITATIONS_TABLE].
 *
 * @property CHANNELS_INVITATIONS_MAX_USES The name of the column in the table [CHANNELS_INVITATIONS_TABLE].
 */
private const val CHANNELS_INVITATIONS_MAX_USES = "maxUses"

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
        return createChannel(
            id = id,
            owner = owner,
            name = name,
            accessControl = accessControl,
            visibility = Visibility.valueOf(visibility.uppercase()),
        )
    }

    /**
     * Converts the [ResultSet] to a [ChannelInvitation].
     */
    private fun ResultSet.toChannelInvitation(): ChannelInvitation {
        val id = getInt(CHANNELS_INVITATIONS_CHANNEL_ID).toUInt()
        val expirationDate = getDate(CHANNELS_INVITATIONS_EXPIRATION_DATE).toLocalDate()
        val invitation = getString(CHANNELS_INVITATIONS_INVITATION)
        val accessControl = AccessControl.valueOf(getString(CHANNELS_INVITATIONS_ACCESS_CONTROL).uppercase())
        val maxUses = getInt(CHANNELS_INVITATIONS_MAX_USES)
        return ChannelInvitation(
            channelId = id,
            expirationDate = expirationDate,
            invitationCode = UUID.fromString(invitation),
            accessControl = accessControl,
            maxUses = maxUses.toUInt(),
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
     *
     * - The [PreparedStatement] must have the following parameters: owner, name, accessControl, visibility.
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

    override fun createChannel(channel: Channel): Channel? {
        val insertQuery =
            """
            INSERT INTO channels (owner, name, accessControl, visibility)
            VALUES (?, ?, ?, ?) RETURNING id
            """.trimIndent()
        val stm = connection.prepareStatement(insertQuery)
        stm.setInfo(channel)
        val rs = stm.executeQuery()
        return if (rs.next()) {
            when (channel) {
                is Channel.Public -> channel.copy(channelId = rs.getInt(CHANNELS_TABLE_ID).toUInt())
                is Channel.Private -> channel.copy(channelId = rs.getInt(CHANNELS_TABLE_ID).toUInt())
            }
        } else {
            null
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
            	channel_id, channel_name, channel_owner, channel_accessControl,
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
        accessControl: AccessControl,
    ) {
        val insertQuery =
            """
            INSERT INTO channel_members (channel, member, accessControl)
            VALUES (?, ?, ?)
            """.trimIndent()
        val stm = connection.prepareStatement(insertQuery)
        var idx = 1
        stm.setInt(idx++, channelId.toInt())
        stm.setInt(idx++, userId.toInt())
        stm.setString(idx, accessControl.toString())
        stm.executeUpdate()
    }

    override fun isUserInChannel(
        channelId: UInt,
        userId: UInt,
    ): Boolean {
        val selectQuery =
            """
            SELECT member from channel_members
            WHERE channel = ? AND member = ?
            """.trimIndent()
        val stm = connection.prepareStatement(selectQuery)
        var idx = 1
        stm.setInt(idx++, channelId.toInt())
        stm.setInt(idx, userId.toInt())
        val rs = stm.executeQuery()
        return rs.next()
    }

    override fun findInvitation(channelId: UInt): ChannelInvitation? {
        val selectQuery =
            """
            SELECT channel_id, expirationdate, invitation, accessControl, maxuses
            FROM channels_invitations
            WHERE channel_id = ?
            """.trimIndent()
        val stm = connection.prepareStatement(selectQuery)
        stm.setInt(1, channelId.toInt())
        val rs = stm.executeQuery()
        return if (rs.next()) {
            rs.toChannelInvitation()
        } else {
            null
        }
    }

    override fun updateInvitation(invitation: ChannelInvitation) {
        val updateQuery =
            """
            UPDATE channels_invitations
            SET maxuses = ?
            WHERE channel_id = ?
            """.trimIndent()
        val stm = connection.prepareStatement(updateQuery)
        var idx = 1
        stm.setInt(idx++, invitation.maxUses.toInt())
        stm.setInt(idx, invitation.channelId.toInt())
        stm.executeUpdate()
    }

    override fun deleteInvitation(channelId: UInt) {
        val deleteQuery =
            """
            DELETE FROM channels_invitations
            WHERE channel_id = ?
            """.trimIndent()
        val stm = connection.prepareStatement(deleteQuery)
        stm.setInt(1, channelId.toInt())
        stm.executeUpdate()
    }

    override fun createInvitation(invitation: ChannelInvitation) {
        val insertQuery =
            """
            INSERT INTO channels_invitations (channel_id, expirationDate, invitation, accessControl, maxUses)
            VALUES (?, ?, ?, ?, ?)
            """.trimIndent()
        val stm = connection.prepareStatement(insertQuery)
        var idx = 1
        stm.setInt(idx++, invitation.channelId.toInt())
        stm.setObject(idx++, invitation.expirationDate)
        stm.setString(idx++, invitation.invitationCode.toString())
        stm.setString(idx++, invitation.accessControl.toString())
        stm.setInt(idx, invitation.maxUses.toInt())
        stm.executeUpdate()
    }

    override fun findUserAccessControl(
        channelId: UInt,
        userId: UInt,
    ): AccessControl? {
        val selectQuery =
            """
            SELECT accessControl from channel_members
            WHERE channel = ? AND member = ?
            """.trimIndent()
        val stm = connection.prepareStatement(selectQuery)
        var idx = 1
        stm.setInt(idx++, channelId.toInt())
        stm.setInt(idx, userId.toInt())
        val rs = stm.executeQuery()
        return if (rs.next()) {
            AccessControl.valueOf(rs.getString(1).uppercase())
        } else {
            null
        }
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

        val deleteFromInvitationsQuery =
            """
            DELETE FROM channels_invitations
            """.trimIndent()
        connection
            .prepareStatement(deleteFromInvitationsQuery)
            .executeUpdate()

        connection
            .prepareStatement(deleteFromChannelMembersQuery)
            .executeUpdate()
        connection
            .prepareStatement(deleteQuery)
            .executeUpdate()
    }
}