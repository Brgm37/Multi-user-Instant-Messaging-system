package transaction

import ChannelRepositoryInterface
import UserRepositoryInterface

interface Transaction {
	val channelRepo: ChannelRepositoryInterface
	val userRepo: UserRepositoryInterface
	fun rollback()
}