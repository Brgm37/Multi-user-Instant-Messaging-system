package services

import errors.ChannelError
import errors.ChannelError.*
import interfaces.ChannelServicesInterface
import jakarta.inject.Inject
import jakarta.inject.Named
import model.*
import org.example.transactionManager.TransactionManager
import utils.Either
import utils.failure
import utils.success

@Named("ChannelServices")
class ChannelServices @Inject constructor(
	@Named("TransactionManagerJDBC") private val repoManager: TransactionManager,
): ChannelServicesInterface {
	override fun createChannel(
		owner: UInt,
		name: String,
		accessControl: String,
		visibility: String,
	): Either<ChannelError, Channel> {
		if (name.isEmpty() || accessControl.isEmpty() || visibility.isEmpty()) {
			return failure(InvalidChannelInfo)
		}
		if (accessControl.uppercase() !in AccessControl.entries.map(AccessControl::name)) {
			return failure(InvalidChannelInfo)
		}
		if (visibility.uppercase() !in Visibility.entries.map(Visibility::name)) {
			return failure(InvalidChannelInfo)
		}
		return repoManager.run {
			val user = userRepo.findById(owner) ?: return@run failure(UserNotFound)
			val id = requireNotNull(user.uId) { "User id is null" }
			val channel = Channel.createChannel(
				owner = UserInfo(id, user.username),
				name = ChannelName(name, user.username),
				accessControl = AccessControl.valueOf(accessControl.uppercase()),
				visibility = Visibility.valueOf(visibility.uppercase())
			)
			success(channelRepo.createChannel(channel))
		}
	}

	override fun deleteChannel(id: UInt): Either<ChannelError, Unit> =
		repoManager
			.run {
				channelRepo.findById(id) ?: return@run failure(ChannelNotFound)
				channelRepo.deleteById(id)
				success(Unit)
			}

	override fun getChannel(id: UInt): Either<ChannelError, Channel> =
		repoManager
			.run {
				val channel = channelRepo.findById(id) ?: return@run failure(ChannelNotFound)
				success(channel)
			}

	override fun getChannels(owner: UInt): Either<UnableToGetChannel, Sequence<Channel>> {
		TODO("Not yet implemented")
	}

	override fun getChannels(): Either<ChannelError, Sequence<Channel>> =
		repoManager
			.run {
				val channels = channelRepo.findAll()
				success(channels)
			}

	override fun latestMessages(id: UInt, quantity: Int): Sequence<Message> {
		TODO("Not yet implemented")
	}
}
