package com.example.appWeb.model.dto.output.message

import model.messages.Message

/**
 * Represents a MessageOutputModel.
 *
 * @property id the message’s identifier (unique).
 * @property message the message.
 * @property user the user that created the message.
 * @property channel the channel in which it is present.
 * @property creationTime the timestamp of when the message is created.
 */
data class MessageOutputModel(
    val id: UInt,
    val message: String,
    val user: UInt,
    val channel: UInt,
    val creationTime: String,
) {
    companion object {
        fun fromDomain(message: Message): MessageOutputModel =
            MessageOutputModel(
                id = requireNotNull(message.msgId) { "Message id is null" },
                message = message.msg,
                user = message.user.uId,
                channel = message.channel.uId,
                creationTime = message.creationTime.toString(),
            )
    }
}
