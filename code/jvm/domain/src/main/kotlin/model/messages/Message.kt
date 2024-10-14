package model.messages

import model.channels.ChannelInfo
import model.users.UserInfo
import java.time.LocalDateTime

/**
 * Represents a Message.
 *
 * @property msgId The unique identifier of the message.
 * @param msg the message.
 * @param user the user that created the message.
 * @param channel the channel in which it is present.
 * @param creationTime the timestamp of when the message is created.
 */

data class Message(
    val msgId: UInt? = null,
    val msg: String,
    val user: UserInfo,
    val channel: ChannelInfo,
    val creationTime: LocalDateTime = LocalDateTime.now(),
) {
    init {
        require(msg.isNotBlank()) { "The message cannot be empty." }
    }
}