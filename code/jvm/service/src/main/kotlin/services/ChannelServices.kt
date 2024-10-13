package services

import TransactionManager
import errors.ChannelError
import errors.ChannelError.ChannelNotFound
import errors.ChannelError.InvalidChannelAccessControl
import errors.ChannelError.InvalidChannelInfo
import errors.ChannelError.InvalidChannelVisibility
import errors.ChannelError.UnableToCreateChannel
import errors.ChannelError.UserNotFound
import interfaces.ChannelServicesInterface
import jakarta.inject.Inject
import jakarta.inject.Named
import model.channels.AccessControl
import model.channels.Channel
import model.channels.Channel.Companion.createChannel
import model.channels.ChannelName
import model.channels.Visibility
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
                return failure(InvalidChannelAccessControl)
            }
            if (visibility.uppercase() !in Visibility.entries.map(Visibility::name)) {
                return failure(InvalidChannelVisibility)
            }
            return repoManager.run {
                val user = userRepo.findById(owner) ?: return@run failure(UserNotFound)
                val id = checkNotNull(user.uId) { "User id is null" }
                val channel =
                    createChannel(
                        owner = UserInfo(id, user.username),
                        name = ChannelName(name, user.username),
                        accessControl = AccessControl.valueOf(accessControl.uppercase()),
                        visibility = Visibility.valueOf(visibility.uppercase()),
                    )
                val createdChannel = channelRepo.createChannel(channel)
                if (createdChannel == null) {
                    failure(UnableToCreateChannel)
                } else {
                    success(createdChannel)
                }
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
                    val id = checkNotNull(user.uId) { "User id is null" }
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
    }
