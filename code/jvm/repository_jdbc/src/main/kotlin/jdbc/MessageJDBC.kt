package jdbc

import MessageRepositoryInterface
import model.channels.ChannelInfo
import model.channels.toChannelName
import model.messages.Message
import model.users.UserInfo
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException

/**
 * MessageJDBC is a JDBC implementation of MessageRepositoryInterface
 * @property connection a JDBC Connection
 */
class MessageJDBC(
    private val connection: Connection,
) : MessageRepositoryInterface {
    private fun ResultSet.toMessage(): Message {
        val author =
            UserInfo(
                uId = getInt("authorId").toUInt(),
                username = getString("authorUsername"),
            )
        val channel =
            ChannelInfo(
                channelId = getInt("msgChannelId").toUInt(),
                channelName = getString("msgChannelName").toChannelName(),
            )
        return Message(
            msgId = getInt("msgId").toUInt(),
            msg = getString("msgContent"),
            user = author,
            channel = channel,
            creationTime = getTimestamp("msgTimestamp"),
        )
    }

    private fun ResultSet.toMessageList(): List<Message> {
        val messages = mutableListOf<Message>()
        while (next()) {
            messages.add(toMessage())
        }
        return messages
    }

    override fun createMessage(message: Message): Message {
        val insertQuery =
            """
            INSERT INTO messages (content, author, channel, timestamp)
            VALUES (?, ?, ?, ?) RETURNING id
            """.trimIndent()
        val stm = connection.prepareStatement(insertQuery)
        var idx = 1
        stm.setString(idx++, message.msg)
        stm.setInt(idx++, message.user.uId.toInt())
        stm.setInt(idx++, message.channel.channelId.toInt())
        stm.setTimestamp(idx, message.creationTime)
        val rs = stm.executeQuery()
        if (rs.next()) {
            return message.copy(msgId = rs.getInt("id").toUInt())
        } else {
            throw SQLException("Failed to create message")
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
            ORDER BY msgTimestamp DESC
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
        offset: Int,
        limit: Int,
    ): List<Message> {
        val selectQuery =
            """
            SELECT 
                msgId, msgChannelId, msgContent, msgauthorid, msgTimestamp,
                msgChannelName, msgAuthorUsername
            FROM v_message
            """.trimIndent()
        val stm = connection.prepareStatement(selectQuery)
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
        stm.setString(idx++, entity.msg)
        stm.setString(idx++, entity.user.username)
        stm.setString(idx++, entity.channel.channelName.fullName)
        stm.setString(idx++, entity.creationTime.toString())
        val id = requireNotNull(entity.msgId) { "Message id is null" }
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