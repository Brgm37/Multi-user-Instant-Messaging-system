package mem

import Transaction
import TransactionManager

/**
 * In-memory implementation of [TransactionManager].
 */
class TransactionManagerInMem : TransactionManager {
    private val channelRepo = ChannelInMem()
    private val userRepo = UserInMem()
    private val messageRepo = MessageInMem()

    override fun <R> run(block: Transaction.() -> R): R = block(TransactionInMem(channelRepo, userRepo, messageRepo))
}