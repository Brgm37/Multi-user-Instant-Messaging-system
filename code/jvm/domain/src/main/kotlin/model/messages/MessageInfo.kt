package model.messages

import java.sql.Timestamp

class MessageInfo(
    val msgId: UInt? = null,
    val msg: String,
    val creationTime: Timestamp,
) {
    init {
        require(msg.isNotBlank()) { "The message cannot be empty." }
    }
}