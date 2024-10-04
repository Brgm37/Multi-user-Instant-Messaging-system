package org.example.transaction

import ChannelRepositoryInterface
import UserRepositoryInterface

interface Transaction {
	val channelRepo: ChannelRepositoryInterface
	val userRepo: UserRepositoryInterface
	fun <R> rollback(error: R) : R
}