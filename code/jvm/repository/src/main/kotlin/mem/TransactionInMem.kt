package mem

import ChannelRepositoryInterface
import MessageRepositoryInterface
import Transaction
import UserRepositoryInterface

/**
 * In-memory implementation of [Transaction].
 */
class TransactionInMem(
    override val channelRepo: ChannelRepositoryInterface,
    override val userRepo: UserRepositoryInterface,
    override val messageRepo: MessageRepositoryInterface,
) : Transaction {
    override fun rollback() = throw UnsupportedOperationException()
}