package services

import TransactionManager
import errors.ChannelError.ChannelNotFound
import errors.ChannelError.UserNotFound
import errors.Error
import interfaces.SseServiceInterface
import jakarta.inject.Named
import model.messages.Message
import utils.Either
import utils.failure
import utils.success

@Named("SseServices")
class SseServices(
    private val repoManager: TransactionManager,
) : SseServiceInterface {
    override fun isUserInChannel(
        cId: UInt,
        uId: UInt,
    ): Either<Error, Boolean> =
        repoManager.run {
            userRepo.findById(uId) ?: return@run failure(UserNotFound)
            channelRepo.findById(cId) ?: return@run failure(ChannelNotFound)
            success(channelRepo.isUserInChannel(cId, uId))
        }

    override fun emitAllMessages(
        uId: UInt,
        lastEventId: UInt,
        emitter: (Message) -> Unit,
    ) {
        repoManager.run { messageRepo.emitAllMessages(uId, lastEventId, emitter) }
    }
}