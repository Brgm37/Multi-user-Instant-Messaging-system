package services

import TransactionManager
import errors.ChannelError
import errors.ChannelError.ChannelNotFound
import errors.ChannelError.InvalidChannelInfo
import errors.ChannelError.InvalidChannelVisibility
import errors.ChannelError.UserNotFound
import interfaces.ChannelServicesInterface
import jakarta.inject.Inject
import jakarta.inject.Named
import model.channels.AccessControl
import model.channels.Channel
import model.channels.ChannelName
import model.channels.Visibility
import model.messages.Message
import model.users.UserInfo
import utils.Either
import utils.failure
import utils.success

@Named("ChannelServices")
class ChannelServices
    @Inject
    constructor(
        @Named("TransactionManagerJDBC") private val repoManager: TransactionManager,
    ) : ChannelServicesInterface {
        override fun createChannel(
            owner: UInt,
            name: String,
            accessControl: String,
            visibility: String,
        ): Either<ChannelError, Channel> {
            if (name.isEmpty() || visibility.isEmpty() || accessControl.isEmpty()) {
                return failure(InvalidChannelInfo)
            }
            if (accessControl.uppercase() !in AccessControl.entries.map(AccessControl::name)) {
                return failure(InvalidChannelInfo)
            }
            if (visibility.uppercase() !in Visibility.entries.map(Visibility::name)) {
                return failure(InvalidChannelVisibility)
            }
            return repoManager.run {
                val user = userRepo.findById(owner) ?: return@run failure(UserNotFound)
                val id = checkNotNull(user.uId) { "User id is null" }
                val channel =
                    Channel
                        .createChannel(
                            owner = UserInfo(id, user.username),
                            name = ChannelName(name, user.username),
                            accessControl = AccessControl.valueOf(accessControl.uppercase()),
                            visibility = Visibility.valueOf(visibility.uppercase()),
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
                    // TODO: add messages to channel
                    val channel = channelRepo.findById(id) ?: return@run failure(ChannelNotFound)
                    success(channel)
                }

        override fun getChannels(
            owner: UInt,
            offset: Int,
            limit: Int,
        ): Either<ChannelError, List<Channel>> =
            repoManager
                .run {
                    val user = userRepo.findById(owner) ?: return@run failure(UserNotFound)
                    val id = requireNotNull(user.uId) { "User id is null" }
                    val channels = channelRepo.findByUserId(id, offset, limit)
                    success(channels)
                }

        override fun getChannels(
            offset: Int,
            limit: Int,
        ): Either<ChannelError, List<Channel>> =
            repoManager
                .run {
                    val channels = channelRepo.findAll(offset, limit)
                    success(channels)
                }

        override fun latestMessages(
            id: UInt,
            offset: Int,
            limit: Int,
        ): Either<ChannelError, List<Message>> {
            TODO("Not yet implemented")
        }
    }
