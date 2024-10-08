interface Transaction {
	val channelRepo: ChannelRepositoryInterface
	val userRepo: UserRepositoryInterface
	val messageRepo: MessageRepositoryInterface
	fun rollback()
}