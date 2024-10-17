package mem

import MessageRepositoryInterface
import model.messages.Message

/**
 * In-memory implementation of the message repository
 */

class MessageInMem : MessageRepositoryInterface {
    private val messages = mutableListOf<Message>()
    private val channelMessages = mutableMapOf<UInt, MutableList<UInt>>()
    private var nextId = 1u

    override fun createMessage(message: Message): Message {
        val newMessage = message.copy(msgId = nextId++)
        messages.add(message)
        val mId = checkNotNull(newMessage.msgId) { "Message id is null" }
        channelMessages.getOrPut(message.channel.channelId) { mutableListOf() }.add(mId)
        return newMessage
    }

    override fun findMessagesByChannelId(
        channelId: UInt,
        limit: UInt,
        offset: UInt,
    ): List<Message> {
        val messageIds = channelMessages[channelId] ?: return emptyList()
        return messageIds
            .drop(offset.toInt())
            .take(limit.toInt())
            .mapNotNull { msgId -> messages.find { it.msgId == msgId } }
    }

    override fun findById(id: UInt): Message? = messages.find { it.msgId == id }

    override fun findAll(
        offset: Int,
        limit: Int,
    ): List<Message> = messages.drop(offset).take(limit)

    override fun save(entity: Message) {
        messages.removeIf { it.msgId == entity.msgId }
        messages.add(entity)
    }

    override fun deleteById(id: UInt) {
        messages.removeIf { it.msgId == id }
    }

    override fun clear() {
        messages.clear()
        channelMessages.clear()
    }
}