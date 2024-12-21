package services

import TransactionManager
import interfaces.ChannelSseInterface
import jakarta.inject.Named

@Named("ChannelSseServices")
class ChannelSseServices(
    private val repoManager: TransactionManager,
) : ChannelSseInterface {
    override fun isUserInChannel(
        uId: UInt,
        cId: UInt,
    ): Boolean =
        repoManager.run {
            userRepo.findById(uId) ?: return@run false
            channelRepo.findById(cId) ?: return@run false
            channelRepo.isUserInChannel(cId, uId)
        }
}