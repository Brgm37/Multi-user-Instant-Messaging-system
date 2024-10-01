package services

import ChannelRepositoryInterface
import UserRepositoryInterface
import errors.Error
import interfaces.UserServicesInterface
import utils.Either
import model.User
import utils.failure
import utils.success

class UserServices(
	private val userRepo: UserRepositoryInterface,
	private val channelRepo: ChannelRepositoryInterface
): UserServicesInterface {
	override fun createUser(user: User): Either<Error, User> {
		val createdUser = userRepo.createUser(user) ?: return failure(Error.UserAlreadyExists)
		return success(createdUser)
	}

	override fun deleteUser(id: UInt): Either<Error, String> {
		userRepo.deleteById(id)
		return success("User deleted successfully")
	}

	override fun getUser(id: UInt): Either<Error, User> {
		val user = userRepo.findById(id) ?: return failure(Error.UserNotFound)
		return success(user)
	}

	override fun joinChannel(userId: UInt, channelId: UInt): Either<Error, String> {
		val user = userRepo.findById(userId) ?: return failure(Error.UserNotFound)
		val channel = channelRepo.findById(channelId) ?: return failure(Error.ChannelNotFound)
		user.uId?.let { uId -> channel.id?.let { cId -> userRepo.joinChannel(uId, cId) } }
		return success("User joined channel successfully")
	}

}