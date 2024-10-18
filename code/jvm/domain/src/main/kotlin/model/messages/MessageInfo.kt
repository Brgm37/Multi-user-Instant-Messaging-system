package model.messages

import java.sql.Timestamp

/**
 * Represents a MessageInfo.
 *
 * @property msgId The unique identifier of the message.
 * @property msg the message.
 * @property creationTime the timestamp of when the message is created.
 */
data class MessageInfo(
    val msgId: UInt? = null,
    val msg: String,
    val creationTime: Timestamp,
) {
    init {
        require(msg.isNotBlank()) { "The message cannot be empty." }
    }
}