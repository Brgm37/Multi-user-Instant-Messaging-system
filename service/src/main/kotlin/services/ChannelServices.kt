package services

import ChannelRepositoryInterface
import interfaces.ChannelServicesInterface
import interfaces.ownerInfo
import model.AccessControl
import model.Channel
import model.ChannelName
import model.Message
import model.UserInfo

//TODO: Improve the implementation of the class
//TODO: Better error handling
class ChannelServices(
	private val channelRepo: ChannelRepositoryInterface
): ChannelServicesInterface {
	override fun createChannel(
		owner: ownerInfo,
		name: String,
		accessControl: String,
		visibility: String,
	): Channel {
		val (ownerName, ownerId) = owner
		require(name.isNotBlank()) { "Channel name cannot be blank" }
		require(accessControl.isNotBlank()) { "Channel access control cannot be blank" }
		require(visibility.isNotBlank()) { "Channel visibility cannot be blank" }
		require(ownerId > 0u) { "Owner id must be greater than 0" }
		require(ownerName.isNotBlank()) { "Owner username cannot be blank" }
		val channel =
			if (visibility.uppercase() == "PUBLIC") {
				Channel.Public(
					owner = UserInfo(ownerId, ownerName),
					name = ChannelName(name, ownerName),
					accessControl = AccessControl.valueOf(accessControl.uppercase())
				)
			}
			else {
				Channel.Private(
					owner = UserInfo(ownerId, ownerName),
					name = ChannelName(name, ownerName),
					accessControl = AccessControl.valueOf(accessControl.uppercase())
				)
			}
		return channelRepo.createChannel(channel)
	}

	override fun deleteChannel(id: UInt) {
		channelRepo.deleteById(id)
	}

	override fun getChannel(id: UInt): Channel {
		return channelRepo.findById(id) ?: throw NoSuchElementException("Channel not found.")
	}

	override fun getChannels(owner: UInt): Sequence<Channel> {
		TODO("Not yet implemented")
	}

	override fun getChannels(): Sequence<Channel> {
		return channelRepo.findAll()
	}

	override fun lastMessages(id: UInt, quantity: Int): Sequence<Message> {
		TODO("Not yet implemented")
	}
}