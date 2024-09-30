package services.database

import ChannelRepositoryInterface
import model.Channel

class ChannelDataBaseMock(
	private val id: UInt,
	private var initList: Iterable<Channel>,
): ChannelRepositoryInterface {

	override fun createChannel(channel: Channel): Channel {
		return when (channel) {
			is Channel.Public -> channel.copy(id = id)
			is Channel.Private -> channel.copy(id = id)
		}
	}

	override fun findById(id: UInt): Channel? {
		return initList.firstOrNull { it.id == id }
	}

	override fun findAll(): Sequence<Channel> {
		return initList.asSequence()
	}

	override fun save(entity: Channel) {
		initList = initList.map {
			if (it.id == entity.id) entity
			else it
		}
	}

	override fun deleteById(id: UInt) {
		initList = initList.filter { it.id != id }
	}
}