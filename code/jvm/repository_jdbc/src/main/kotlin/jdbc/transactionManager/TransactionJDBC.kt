package jdbc.transactionManager

import ChannelRepositoryInterface
import Transaction
import UserRepositoryInterface
import jdbc.ChannelJDBC
import jdbc.UserJDBC

class TransactionJDBC(
	private val connection: java.sql.Connection,
) : Transaction {
	override val channelRepo: ChannelRepositoryInterface = ChannelJDBC(connection)
	override val userRepo: UserRepositoryInterface = UserJDBC(connection)

	override fun rollback() {
		connection.rollback()
	}
}
