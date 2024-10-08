package jdbc

import MessageRepositoryInterface
import model.ChannelName
import model.Message
import model.UserInfo
import java.sql.ResultSet
import java.sql.SQLException

class MessageJDBC (envName: String
) : MessageRepositoryInterface, JDBC(envName) {

	private fun ResultSet.toMessage(): Message {
		val author = UserInfo(
			uId = getInt("authorId").toUInt(),
			username = getString("authorUsername"),
		)
		val channel = ChannelInfo( uId = getInt("msgChannelId").toUInt(),
			getString("msgChannelName").toChannelName())
		)
		return Message(
			id = getInt("msgId").toUInt(),
			content = getString("msgContent"),
			author = author,
			channel = channel,
			timestamp = getTimestamp("msgTimestamp").toLocalDateTime()
		)
	}

	override fun createMessage(message: Message): Message {
		return connect { connection ->
			val insertQuery = """
				INSERT INTO messages (content, author, channel, timestamp)
				VALUES (?, ?, ?, ?) RETURNING id
			""".trimIndent()
			val stm = connection.prepareStatement(insertQuery)
			var idx = 1
			stm.setString(idx++, message.msg)
			stm.setString(idx++, message.user.username)
			stm.setString(idx++, message.channel.name)
			stm.setString(idx, message.creationTime.toString())
			val rs = stm.executeQuery()
			if (rs.next()) {
				return@connect message.copy(msgId = rs.getInt("id").toUInt())
			} else {
				throw SQLException("Failed to create message")
			}
		}
	}
	//TODO: falar com arthur sobre channel id -- msgchannel equivalente a msgChannelID
	//caso dele channel_owner -> owner_id
	override fun findById(id: UInt): Message? {
		return connect { connection ->
			val selectQuery = """
				SELECT msgId, msgChannel, msgContent, msgAuthor, msgTimestamp,
				 msgChannelName, authorUsername
				FROM v_message
				WHERE id = ?
			""".trimIndent()
			val stm = connection.prepareStatement(selectQuery)
			stm.setInt(1, id.toInt())
			val rs = stm.executeQuery()
			if (rs.next()){
				return@connect rs.toMessage()
			}else
				null
		}
	}

	override fun findAll(): Sequence<Message> {
		return connect { connection ->
			val selectQuery = """
				SELECT msgId, msgChannel, msgContent, msgAuthor, msgTimestamp,
				 msgChannelName, authorUsername
				FROM v_message
			""".trimIndent()
			val stm = connection.prepareStatement(selectQuery)
			val rs = stm.executeQuery()
			sequence {
				while (rs.next()) {
					yield(rs.toMessage())
				}
			}
		}
	}

	//TODO: user format
	override fun save(entity: Message) {
		connect { connection ->
			val updateQuery = """
                UPDATE messages
                SET channel = ?, author = ?, content = ?, timestamp = ?
                WHERE id = ?
            """.trimIndent()
			val stm = connection.prepareStatement(updateQuery)
			var idx = 1
			stm.setString(idx++, entity.msg)
			stm.setString(idx++, entity.user.username)
			stm.setString(idx++, entity.channel.name)
			stm.setString(idx++, entity.creationTime.toString())
			val id = requireNotNull(entity.id) { "Message id is null"}
			stm.setInt(idx, id.toInt())
			stm.executeUpdate()
		}
	}

	override fun deleteById(id: UInt) {
		connect { connection ->
			val deleteQuery = """
				DELETE FROM messages
				WHERE id = ?
			""".trimIndent()
			val stm = connection.prepareStatement(deleteQuery)
			stm.setInt(1, id.toInt())
			stm.executeUpdate()
		}
	}

}