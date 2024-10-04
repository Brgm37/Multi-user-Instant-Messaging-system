package services

import errors.ChannelError
import errors.ChannelError.*
import interfaces.ChannelServicesInterface
import jakarta.inject.Named
import model.*
import org.example.transactionManager.TransactionManager
import services.param.OwnerInfoParam
import utils.Either
import utils.failure
import utils.success

@Named("ChannelServices")
class ChannelServices(
	private val repoManager: TransactionManager,
): ChannelServicesInterface {
	override fun createChannel(
		owner: OwnerInfoParam,
		name: String,
		accessControl: String,
		visibility: String,
	): Either<ChannelError, Channel> {
		val (username, ownerId) = owner
		if (username.isEmpty() || name.isEmpty() || accessControl.isEmpty() || visibility.isEmpty()) {
			return failure(InvalidChannelInfo)
		}
		if (accessControl.uppercase() !in AccessControl.entries.map(AccessControl::name)) {
			return failure(InvalidChannelInfo)
		}
		if (visibility.uppercase() !in Visibility.entries.map(Visibility::name)) {
			return failure(InvalidChannelInfo)
		}
		val channel = Channel.createChannel(
			owner = UserInfo(ownerId, username),
			name = ChannelName(name, username),
			accessControl = AccessControl.valueOf(accessControl.uppercase()),
			visibility = Visibility.valueOf(visibility.uppercase())
		)
		return repoManager.run(failure(UnableToCreateChannel)) {
			userRepo.findById(ownerId) ?: failure(OwnerNotFound)
			val newChannel = channelRepo.createChannel(channel)
			success(newChannel)
		}
	}

	override fun deleteChannel(id: UInt): Either<ChannelError, Unit> =
		repoManager
			.run(failure(UnableToDeleteChannel)) {
				channelRepo.findById(id) ?: return@run failure(ChannelNotFound)
				channelRepo.deleteById(id)
				success(Unit)
			}

	override fun getChannel(id: UInt): Either<ChannelError, Channel> =
		repoManager
			.run(failure(UnableToGetChannel)) {
				val channel = channelRepo.findById(id) ?: return@run failure(ChannelNotFound)
				success(channel)
			}

	override fun getChannels(owner: UInt): Either<UnableToGetChannel, Sequence<Channel>> {
		TODO("Not yet implemented")
	}

	override fun getChannels(): Either<ChannelError, Sequence<Channel>> =
		repoManager
			.run(failure(UnableToGetChannel)) {
				val channels = channelRepo.findAll()
				success(channels)
			}

	override fun latestMessages(id: UInt, quantity: Int): Sequence<Message> {
		TODO("Not yet implemented")
	}
}