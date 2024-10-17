package jdbc.transactionManager

import ChannelRepositoryInterface
import MessageRepositoryInterface
import Transaction
import UserRepositoryInterface
import jdbc.ChannelJDBC
import jdbc.MessageJDBC
import jdbc.UserJDBC
import utils.encryption.DummyEncrypt
import utils.encryption.Encrypt
import java.sql.Connection

class TransactionJDBC(
    private val connection: Connection,
    encrypt: Encrypt = DummyEncrypt,
) : Transaction {
    override val channelRepo: ChannelRepositoryInterface = ChannelJDBC(connection, encrypt)
    override val userRepo: UserRepositoryInterface = UserJDBC(connection, encrypt)
    override val messageRepo: MessageRepositoryInterface = MessageJDBC(connection, encrypt)

    override fun rollback() {
        connection.rollback()
    }
}