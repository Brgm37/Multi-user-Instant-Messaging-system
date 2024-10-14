package mem

import MessageRepositoryInterface
import model.messages.Message

class MessageInMem : MessageRepositoryInterface {
    override fun createMessage(message: Message): Message {
        TODO("Not yet implemented")
    }

    override fun findMessagesByChannelId(
        channelId: UInt,
        limit: UInt,
        offset: UInt,
    ): List<Message> {
        TODO("Not yet implemented")
    }

    override fun findById(id: UInt): Message? {
        TODO("Not yet implemented")
    }

    override fun findAll(
        offset: Int,
        limit: Int,
    ): List<Message> {
        TODO("Not yet implemented")
    }

    override fun save(entity: Message) {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: UInt) {
        TODO("Not yet implemented")
    }

    override fun clear() {
        // TODO: Implement this method
        return
    }
}