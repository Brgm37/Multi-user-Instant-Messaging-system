package services

import ChannelRepositoryInterface
import errors.ChannelError
import errors.ChannelError.ChannelNotFound
import interfaces.ChannelServicesInterface
import interfaces.ownerInfo
import jakarta.inject.Named
import model.AccessControl
import model.Channel
import model.ChannelName
import model.Message
import model.UserInfo
import utils.Either
import utils.failure
import utils.success

//TODO: Class implementation must be improved
//TODO: Improve the error handling
@Named("ChannelServices")
class ChannelServices(
	private val channelRepo: ChannelRepositoryInterface
): ChannelServicesInterface {
	override fun createChannel(
		owner: ownerInfo,
		name: String,
		accessControl: String,
		visibility: String,
	): Either<ChannelError, Channel> {
		val (ownerName, ownerId) = owner
		//TODO: Add a try-catch block to handle the exception
		require(name.isNotBlank()) { "Channel name cannot be blank" }
		require(accessControl.isNotBlank()) { "Channel access control cannot be blank" }
		require(visibility.isNotBlank()) { "Channel visibility cannot be blank" }
		require(ownerId > 0u) { "Owner id must be greater than 0" }
		require(ownerName.isNotBlank()) { "Owner username cannot be blank" }
		val channel = Channel.createChannel(
			owner = UserInfo(ownerId, ownerName),
			name = ChannelName(name, ownerName),
			accessControl = AccessControl.valueOf(accessControl),
			visibility = visibility
		)
		//TODO: Add error case
		return success(channelRepo.createChannel(channel))
	}

	override fun deleteChannel(id: UInt): Either<ChannelError, Unit> {
		//TODO: Add error case
		channelRepo.deleteById(id)
		return success(Unit)
	}

	override fun getChannel(id: UInt): Either<ChannelError, Channel> {
		val channel = channelRepo.findById(id)
		return if (channel != null) {
			success(channel)
		} else {
			failure(ChannelNotFound)
		}
	}

	override fun getChannels(owner: UInt): Either<ChannelError.UnableToGetChannel, Sequence<Channel>> {
		TODO("Not yet implemented")
	}

	override fun getChannels(): Either<ChannelError, Sequence<Channel>> {
		//TODO: Add error case
		return success(channelRepo.findAll())
	}

	override fun latestMessages(id: UInt, quantity: Int): Sequence<Message> {
		TODO("Not yet implemented")
	}
}