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
import utils.encryption.DummyEncrypt
import utils.encryption.Encrypt
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
 * @property CHANNELS_VIEW_DESCRIPTION The name of the column in the view [CHANNELS_VIEW].
 */
private const val CHANNELS_VIEW_DESCRIPTION = "channel_description"

/**
 * The name of the column in the view [CHANNELS_VIEW].
 *
 * @property CHANNELS_VIEW_ICON The name of the column in the view [CHANNELS_VIEW].
 */
private const val CHANNELS_VIEW_ICON = "channel_icon"

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
private const val CHANNELS_INVITATIONS_EXPIRATION_DATE = "expiration_date"

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
private const val CHANNELS_INVITATIONS_ACCESS_CONTROL = "access_control"

/**
 * The name of the column in the table [CHANNELS_INVITATIONS_TABLE].
 *
 * @property CHANNELS_INVITATIONS_MAX_USES The name of the column in the table [CHANNELS_INVITATIONS_TABLE].
 */
private const val CHANNELS_INVITATIONS_MAX_USES = "max_uses"

/**
 * The name of the table in the database.
 */
private const val CHANNEL_MEMBERS = "channel_member"

/**
 * The name of the column in the table [CHANNEL_MEMBERS].
 */
private const val CHANNEL_MEMBERS_ACCESS_CONTROL = "access_control"

/**
 * A JDBC implementation of the [ChannelRepositoryInterface].
 * @property connection The connection to the database.
 * @property encrypt The encryption algorithm.
 * @constructor Creates a [ChannelJDBC] with the given connection.
 */
class ChannelJDBC(
    private val connection: Connection,
    private val encrypt: Encrypt = DummyEncrypt,
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
        val description = getString(CHANNELS_VIEW_DESCRIPTION)
        val icon = getString(CHANNELS_VIEW_ICON)
        return createChannel(
            id = id,
            owner = owner,
            name = name,
            accessControl = accessControl,
            visibility = Visibility.valueOf(visibility.uppercase()),
            description = description,
            icon = icon,
        )
    }

    /**
     * Converts the [ResultSet] to a [ChannelInvitation].
     */
    private fun ResultSet.toChannelInvitation(): ChannelInvitation {
        val id = getInt(CHANNELS_INVITATIONS_CHANNEL_ID).toUInt()
        val expirationDate = getTimestamp(CHANNELS_INVITATIONS_EXPIRATION_DATE)
        val invitation = encrypt.decrypt(getString(CHANNELS_INVITATIONS_INVITATION))
        val accessControl = AccessControl.valueOf(getString(CHANNELS_INVITATIONS_ACCESS_CONTROL).uppercase())
        val maxUses = getInt(CHANNELS_INVITATIONS_MAX_USES)
        return ChannelInvitation(
            cId = id,
            expirationDate = expirationDate,
            invitationCode = UUID.fromString(invitation),
            accessControl = accessControl,
            maxUses = maxUses.toUInt(),
        )
    }

    /**
     * Convert the [ResultSet] into an [AccessControl] or null if nothing is found.
     */
    private fun ResultSet.toAccessControl(): AccessControl? =
        if (next()) {
            AccessControl.valueOf(getString(CHANNEL_MEMBERS_ACCESS_CONTROL))
        } else {
            null
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
        val id = channel.cId
        setString(idx++, channel.description)
        setString(idx++, channel.icon)
        if (id != null) setInt(idx, id.toInt())
    }

    override fun createChannel(channel: Channel): Channel? {
        val insertQuery =
            """
            INSERT INTO channels (owner, name, access_control, visibility, description, icon)
            VALUES (?, ?, ?, ?, ?, ?) RETURNING id
            """.trimIndent()
        val stm = connection.prepareStatement(insertQuery)
        stm.setInfo(channel)
        val rs = stm.executeQuery()
        return if (rs.next()) {
            when (channel) {
                is Channel.Public -> channel.copy(cId = rs.getInt(CHANNELS_TABLE_ID).toUInt())
                is Channel.Private -> channel.copy(cId = rs.getInt(CHANNELS_TABLE_ID).toUInt())
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
            	channel_visibility, owner_name, channel_description, channel_icon
            FROM v_channel JOIN channel_members c on c.channel = v_channel.channel_id
            WHERE member = ?
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
        cId: UInt,
        uId: UInt,
        accessControl: AccessControl,
    ) {
        val insertQuery =
            """
            INSERT INTO channel_members (channel, member, access_control)
            VALUES (?, ?, ?)
            """.trimIndent()
        val stm = connection.prepareStatement(insertQuery)
        var idx = 1
        stm.setInt(idx++, cId.toInt())
        stm.setInt(idx++, uId.toInt())
        stm.setString(idx, accessControl.toString())
        stm.executeUpdate()
    }

    override fun isUserInChannel(
        cId: UInt,
        uId: UInt,
    ): Boolean {
        val selectQuery =
            """
            SELECT member from channel_members
            WHERE channel = ? AND member = ?
            """.trimIndent()
        val stm = connection.prepareStatement(selectQuery)
        var idx = 1
        stm.setInt(idx++, cId.toInt())
        stm.setInt(idx, uId.toInt())
        val rs = stm.executeQuery()
        return rs.next()
    }

    override fun findInvitation(cId: UInt): ChannelInvitation? {
        val selectQuery =
            """
            SELECT channel_id, expiration_date, invitation, access_control, max_uses
            FROM channels_invitations
            WHERE channel_id = ?
            """.trimIndent()
        val stm = connection.prepareStatement(selectQuery)
        stm.setInt(1, cId.toInt())
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
            SET max_uses = ?
            WHERE channel_id = ?
            """.trimIndent()
        val stm = connection.prepareStatement(updateQuery)
        var idx = 1
        stm.setInt(idx++, invitation.maxUses.toInt())
        stm.setInt(idx, invitation.cId.toInt())
        stm.executeUpdate()
    }

    override fun deleteInvitation(cId: UInt) {
        val deleteQuery =
            """
            DELETE FROM channels_invitations
            WHERE channel_id = ?
            """.trimIndent()
        val stm = connection.prepareStatement(deleteQuery)
        stm.setInt(1, cId.toInt())
        stm.executeUpdate()
    }

    override fun createInvitation(invitation: ChannelInvitation) {
        val insertQuery =
            """
            INSERT INTO channels_invitations (channel_id, expiration_date, invitation, access_control, max_uses)
            VALUES (?, ?, ?, ?, ?)
            """.trimIndent()
        val stm = connection.prepareStatement(insertQuery)
        var idx = 1
        stm.setInt(idx++, invitation.cId.toInt())
        stm.setTimestamp(idx++, invitation.expirationDate)
        stm.setString(idx++, encrypt.encrypt(invitation.invitationCode.toString()))
        stm.setString(idx++, invitation.accessControl.toString())
        stm.setInt(idx, invitation.maxUses.toInt())
        stm.executeUpdate()
    }

    override fun findUserAccessControl(
        cId: UInt,
        userId: UInt,
    ): AccessControl? {
        val selectQuery =
            """
            SELECT access_control from channel_members
            WHERE channel = ? AND member = ?
            """.trimIndent()
        val stm = connection.prepareStatement(selectQuery)
        var idx = 1
        stm.setInt(idx++, cId.toInt())
        stm.setInt(idx, userId.toInt())
        val rs = stm.executeQuery()
        return if (rs.next()) {
            AccessControl.valueOf(rs.getString(1).uppercase())
        } else {
            null
        }
    }

    override fun findByName(name: String): Channel? {
        val selectQuery =
            """
            SELECT 
            	channel_id, channel_name, channel_owner, channel_accessControl,
            	channel_visibility, owner_name, channel_description, channel_icon
            FROM v_channel
            WHERE channel_name = ?
            """.trimIndent()
        val stm = connection.prepareStatement(selectQuery)
        stm.setString(1, name)
        val rs = stm.executeQuery()
        return if (rs.next()) {
            rs.toChannel()
        } else {
            null
        }
    }

    override fun findByName(
        name: String,
        offset: UInt,
        limit: UInt,
    ): List<Channel> {
        val selectQuery =
            """
            SELECT 
            	channel_id, channel_name, channel_owner, channel_accessControl,
            	channel_visibility, owner_name, channel_description, channel_icon
            FROM v_channel
            WHERE LOWER(channel_name) LIKE LOWER(?) AND channel_visibility = '${PUBLIC.name}'
            LIMIT ?
            OFFSET ?
            """.trimIndent()
        val stm = connection.prepareStatement(selectQuery)
        var idx = 1
        stm.setString(idx++, "%$name%")
        stm.setInt(idx++, limit.toInt())
        stm.setInt(idx, offset.toInt())
        val rs = stm.executeQuery()
        return rs.toChannelList()
    }

    override fun findByName(
        uId: UInt,
        name: String,
        offset: UInt,
        limit: UInt,
    ): List<Channel> {
        val selectQuery =
            """
            SELECT 
            	channel_id, channel_name, channel_owner, channel_accessControl,
            	channel_visibility, owner_name, channel_description, channel_icon
            FROM v_channel JOIN channel_members c on c.id = v_channel.channel_id
            WHERE member = ? AND compare_partial_name(channel_name, ?)
            LIMIT ?
            OFFSET ?
            """.trimIndent()
        val stm = connection.prepareStatement(selectQuery)
        var idx = 1
        stm.setInt(idx++, uId.toInt())
        stm.setString(idx++, name)
        stm.setInt(idx++, limit.toInt())
        stm.setInt(idx, offset.toInt())
        val rs = stm.executeQuery()
        return rs.toChannelList()
    }

    override fun findAccessControl(
        uid: UInt,
        cId: UInt,
    ): AccessControl? {
        val selectQuery =
            """
            SELECT access_control
            FROM channel_members
            WHERE member = ? AND channel = ?
            """.trimIndent()
        val stm = connection.prepareStatement(selectQuery)
        var idx = 1
        stm.setInt(idx++, uid.toInt())
        stm.setInt(idx, cId.toInt())
        val rs = stm.executeQuery()
        return rs.toAccessControl()
    }

    override fun findByInvitationCode(invitationCode: String): Channel? {
        val selectQuery =
            """
            SELECT 
            	v_channel.channel_id, channel_name, channel_owner, channel_accessControl,
            	channel_visibility, owner_name, channel_description, channel_icon
            FROM v_channel JOIN channels_invitations c on c.channel_id = v_channel.channel_id
            WHERE invitation = ?
            """.trimIndent()
        val stm = connection.prepareStatement(selectQuery)
        stm.setString(1, encrypt.encrypt(invitationCode))
        val rs = stm.executeQuery()
        return if (rs.next()) {
            rs.toChannel()
        } else {
            null
        }
    }

    override fun findPublicChannel(
        uId: UInt,
        offset: UInt,
        limit: UInt,
    ): List<Channel> {
        val selectQuery =
            """
            SELECT 
            	channel_id, channel_name, channel_owner, channel_accessControl,
            	channel_visibility, owner_name, channel_description, channel_icon
            FROM v_channel
            WHERE channel_visibility = '${PUBLIC.name}'
            AND channel_id NOT IN (
                SELECT channel
                FROM channel_members
                WHERE member = ?
            )
            LIMIT ?
            OFFSET ?
            """.trimIndent()
        val stm = connection.prepareStatement(selectQuery)
        var idx = 1
        stm.setInt(idx++, uId.toInt())
        stm.setInt(idx++, limit.toInt())
        stm.setInt(idx, offset.toInt())
        val rs = stm.executeQuery()
        return rs.toChannelList()
    }

    override fun leaveChannel(
        cId: UInt,
        uId: UInt,
    ) {
        val deleteQuery =
            """
            DELETE FROM channel_members
            WHERE channel = ? AND member = ?
            """.trimIndent()
        val stm = connection.prepareStatement(deleteQuery)
        var idx = 1
        stm.setInt(idx++, cId.toInt())
        stm.setInt(idx, uId.toInt())
        stm.executeUpdate()
    }

    override fun findById(id: UInt): Channel? {
        val selectQuery =
            """
            SELECT 
            	channel_id, channel_name, channel_owner, channel_accessControl,
            	channel_visibility, owner_name, channel_description, channel_icon
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
        offset: UInt,
        limit: UInt,
    ): List<Channel> {
        val selectQuery =
            """
            SELECT 
            	channel_id, channel_name, channel_owner, channel_accessControl,
            	channel_visibility, owner_name, channel_description, channel_icon
            FROM v_channel
            WHERE channel_visibility = 'PUBLIC'
            LIMIT ? OFFSET ?
            """.trimIndent()
        val stm = connection.prepareStatement(selectQuery)
        var idx = 1
        stm.setInt(idx++, limit.toInt())
        stm.setInt(idx, offset.toInt())
        val rs = stm.executeQuery()
        return rs.toChannelList()
    }

    override fun save(entity: Channel) {
        val updateQuery =
            """
            UPDATE channels
            SET owner = ?, name = ?, access_control = ?, visibility = ?, description = ?, icon = ?
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