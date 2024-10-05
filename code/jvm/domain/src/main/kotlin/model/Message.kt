package model

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
	val channel: ChannelName,
	val creationTime: LocalDateTime
){
	init {
		require(msg.isNotBlank()) { "The message cannot be empty." }
	}
}
