package model.messages

import model.channels.ChannelInfo
import model.users.UserInfo
import java.sql.Timestamp
import java.time.LocalDateTime

/**
 * Represents a Message.
 *
 * @property msgId The unique identifier of the message.
 * @property msg the message.
 * @property user the user that created the message.
 * @property channel the channel in which it is present.
 * @property creationTime the timestamp of when the message is created.
 * @throws IllegalArgumentException if the message is empty.
 */
data class Message(
    val msgId: UInt? = null,
    val msg: String,
    val user: UserInfo,
    val channel: ChannelInfo,
    val creationTime: Timestamp = Timestamp.valueOf(LocalDateTime.now()),
) {
    init {
        require(msg.isNotBlank()) { "The message cannot be empty." }
    }
}