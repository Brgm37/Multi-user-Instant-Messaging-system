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

/**
 * MessageJDBC is a JDBC implementation of MessageRepositoryInterface
 * @property connection a JDBC Connection
 */
class MessageJDBC(
    private val connection: Connection,
    private val encrypt: Encrypt = DummyEncrypt,
) : MessageRepositoryInterface {
    private fun ResultSet.toMessage(): Message {
        val author =
            UserInfo(
                uId = getInt("msgAuthorId").toUInt(),
                username = getString("msgAuthorUsername"),
            )
        val channel =
            ChannelInfo(
                channelId = getInt("msgChannelId").toUInt(),
                channelName = getString("msgChannelName").toChannelName(),
            )
        return Message(
            msgId = getInt("msgId").toUInt(),
            msg = encrypt.decrypt(getString("msgContent")),
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
        stm.setInt(idx++, message.channel.channelId.toInt())
        stm.setTimestamp(idx, message.creationTime)
        val rs = stm.executeQuery()
        return if (rs.next()) {
            message.copy(msgId = rs.getInt("id").toUInt())
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
        stm.setInt(idx++, entity.channel.channelId.toInt())
        stm.setInt(idx++, entity.user.uId.toInt())
        stm.setString(idx++, encrypt.encrypt(entity.msg))
        stm.setString(idx++, entity.creationTime.toString())
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