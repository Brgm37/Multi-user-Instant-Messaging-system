package jdbc

import MessageRepositoryInterface
import model.channels.ChannelInfo
import model.channels.toChannelName
import model.messages.Message
import model.users.UserInfo
import utils.encryption.DummyEncrypt
import utils.encryption.Encrypt
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Timestamp

/**
 * @property AUTHOR_ID the author id column of the v_message table
 */
private const val AUTHOR_ID = "msgAuthorId"

/**
 * @property AUTHOR_USERNAME the author username column of the v_message table
 */
private const val AUTHOR_USERNAME = "msgAuthorUsername"

/**
 * @property CHANNEL_ID the channel id column of the v_message table
 */
private const val CHANNEL_ID = "msgChannelId"

/**
 * @property CHANNEL_NAME the channel name column of the v_message table
 */
private const val CHANNEL_NAME = "msgChannelName"

/**
 * @property CONTENT the content column of the v_message table
 */
private const val CONTENT = "msgContent"

/**
 * @property ID the id column of the v_message table
 */
private const val ID = "msgId"

/**
 * @property MESSAGE_TABLE_ID id column of the message table
 */
private const val MESSAGE_TABLE_ID = "id"

/**
 * @property TIMESTAMP the timestamp column of the v_message table
 */
private const val TIMESTAMP = "msgTimestamp"

/**
 * MessageJDBC is a JDBC implementation of MessageRepositoryInterface
 *
 * @property connection a JDBC Connection
 * @property encrypt an encryption utility to use
 */
class MessageJDBC(
    private val connection: Connection,
    private val encrypt: Encrypt = DummyEncrypt,
) : MessageRepositoryInterface {
    private fun ResultSet.toMessage(): Message {
        val author =
            UserInfo(
                uId = getInt(AUTHOR_ID).toUInt(),
                username = getString(AUTHOR_USERNAME),
            )
        val channel =
            ChannelInfo(
                cId = getInt(CHANNEL_ID).toUInt(),
                channelName = getString(CHANNEL_NAME).toChannelName(),
            )
        return Message(
            msgId = getInt(ID).toUInt(),
            msg = encrypt.decrypt(getString(CONTENT)),
            user = author,
            channel = channel,
            creationTime = getTimestamp(TIMESTAMP),
        )
    }

    private fun ResultSet.toMessageList(): List<Message> {
        val messages = mutableListOf<Message>()
        while (next()) {
            messages.add(toMessage())
        }
        return messages
    }

    override fun createMessage(message: Message): Message? {
        val insertQuery =
            """
            INSERT INTO messages (content, author, channel, timestamp)
            VALUES (?, ?, ?, ?) RETURNING id
            """.trimIndent()
        val stm = connection.prepareStatement(insertQuery)
        var idx = 1
        stm.setString(idx++, encrypt.encrypt(message.msg))
        stm.setInt(idx++, message.user.uId.toInt())
        stm.setInt(idx++, message.channel.cId.toInt())
        stm.setTimestamp(idx, message.creationTime)
        val rs = stm.executeQuery()
        return if (rs.next()) {
            message.copy(msgId = rs.getInt(MESSAGE_TABLE_ID).toUInt())
        } else {
            null
        }
    }

    override fun findMessagesByChannelId(
        channelId: UInt,
        limit: UInt,
        offset: UInt,
    ): List<Message> {
        val selectQuery =
            """
            SELECT 
                msgId, msgChannelId, msgContent, msgAuthorId, msgTimestamp,
                msgChannelName, msgAuthorUsername
            FROM v_message
            WHERE msgChannelId = ?
            ORDER BY msgTimestamp
            LIMIT ? OFFSET ?
            """.trimIndent()
        val stm = connection.prepareStatement(selectQuery)
        var idx = 1
        stm.setInt(idx++, channelId.toInt())
        stm.setInt(idx++, limit.toInt())
        stm.setInt(idx, offset.toInt())
        val rs = stm.executeQuery()
        return rs.toMessageList()
    }

    override fun emitAllMessages(
        uId: UInt,
        lastEventId: UInt,
        emitter: (Message) -> Unit,
    ) {
        val selectQuery =
            """
            SELECT
                msgId, msgChannelId, msgContent, msgAuthorId, msgTimestamp,
                msgChannelName, msgAuthorUsername
            FROM channel_members JOIN v_message ON channel = msgChannelId 
            WHERE member = ? AND msgId > ?
            ORDER BY msgTimestamp
            """.trimIndent()
        val stm = connection.prepareStatement(selectQuery)
        var idx = 1
        stm.setInt(idx++, uId.toInt())
        stm.setInt(idx, lastEventId.toInt())
        val rs = stm.executeQuery()
        while (rs.next()) {
            emitter(rs.toMessage())
        }
    }

    override fun findMessagesByTimeStamp(
        channelId: UInt,
        timestamp: Timestamp,
        limit: UInt,
    ): List<Message> {
        val selectQuery =
            """
            SELECT 
                msgId, msgChannelId, msgContent, msgAuthorId, msgTimestamp,
                msgChannelName, msgAuthorUsername
            FROM v_message
            WHERE msgChannelId = ? AND msgTimestamp > ?
            ORDER BY msgTimestamp
            LIMIT ?
            """.trimIndent()
        val stm = connection.prepareStatement(selectQuery)
        var idx = 1
        stm.setInt(idx++, channelId.toInt())
        stm.setTimestamp(idx++, timestamp)
        stm.setInt(idx, limit.toInt())
        val rs = stm.executeQuery()
        return rs.toMessageList()
    }

    override fun findById(id: UInt): Message? {
        val selectQuery =
            """
            SELECT 
                msgId, msgChannelId, msgContent, msgAuthorId, msgTimestamp,
                msgChannelName, msgAuthorUsername
            FROM v_message
            WHERE msgid = ?
            """.trimIndent()
        val stm = connection.prepareStatement(selectQuery)
        stm.setInt(1, id.toInt())
        val rs = stm.executeQuery()
        return if (rs.next()) {
            rs.toMessage()
        } else {
            null
        }
    }

    override fun findAll(
        offset: UInt,
        limit: UInt,
    ): List<Message> {
        val selectQuery =
            """
            SELECT 
                msgId, msgChannelId, msgContent, msgauthorid, msgTimestamp,
                msgChannelName, msgAuthorUsername
            FROM v_message
            ORDER BY msgTimestamp
            LIMIT ? OFFSET ?
            """.trimIndent()
        val stm = connection.prepareStatement(selectQuery)
        var idx = 1
        stm.setInt(idx++, limit.toInt())
        stm.setInt(idx, offset.toInt())
        val rs = stm.executeQuery()
        val messages = mutableListOf<Message>()
        while (rs.next()) {
            messages.add(rs.toMessage())
        }
        return messages
    }

    override fun save(entity: Message) {
        val updateQuery =
            """
            UPDATE messages
            SET channel = ?, author = ?, content = ?, timestamp = ?
            WHERE id = ?
            """.trimIndent()
        val stm = connection.prepareStatement(updateQuery)
        var idx = 1
        stm.setInt(idx++, entity.channel.cId.toInt())
        stm.setInt(idx++, entity.user.uId.toInt())
        stm.setString(idx++, encrypt.encrypt(entity.msg))
        stm.setTimestamp(idx++, entity.creationTime)
        val id = checkNotNull(entity.msgId) { "Message id is null" }
        stm.setInt(idx, id.toInt())
        stm.executeUpdate()
    }

    override fun deleteById(id: UInt) {
        val deleteQuery =
            """
            DELETE FROM messages
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
            DELETE FROM messages
            """.trimIndent()
        val stm = connection.prepareStatement(deleteQuery)
        stm.executeUpdate()
    }
}