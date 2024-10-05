package org.example.transaction.jdbc

import ChannelRepositoryInterface
import UserRepositoryInterface
import jdbc.ChannelJDBC
import jdbc.UserJDBC
import org.example.transaction.Transaction

class TransactionJDBC(
	private val connection: java.sql.Connection
) : Transaction {
	//TODO: Should be injected? Or is it fine to create it here?
	override val channelRepo: ChannelRepositoryInterface = ChannelJDBC(connection)
	override val userRepo: UserRepositoryInterface = UserJDBC(connection)
	override fun rollback() {
		connection.rollback()
	}
}