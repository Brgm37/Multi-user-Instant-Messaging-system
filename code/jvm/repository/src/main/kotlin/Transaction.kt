interface Transaction {
	val channelRepo: ChannelRepositoryInterface
	val userRepo: UserRepositoryInterface

	fun rollback()
}
