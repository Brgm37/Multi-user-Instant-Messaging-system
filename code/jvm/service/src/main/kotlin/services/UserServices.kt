package services

import errors.ChannelError
import errors.Error
import errors.UserError
import interfaces.UserServicesInterface
import jakarta.inject.Inject
import jakarta.inject.Named
import utils.Either
import model.User
import transactionManager.TransactionManager
import utils.failure
import utils.success

@Named("UserServices")

class UserServices @Inject constructor(
	@Named("TransactionManagerJDBC")  private val repoManager: TransactionManager,
): UserServicesInterface {
	override fun createUser(user: User): Either<UserError, User> {
		return repoManager.run {
			val createdUser = userRepo.createUser(user) ?: return@run failure(UserError.UserAlreadyExists)
			success(createdUser)
		}
	}

	override fun deleteUser(id: UInt): Either<UserError, Unit> {
		return repoManager.run {
			userRepo.findById(id) ?: return@run failure(UserError.UserNotFound)
			userRepo.deleteById(id)
			success(Unit)
		}
	}

	override fun getUser(id: UInt): Either<UserError, User> {
		return repoManager.run {
			val user = userRepo.findById(id) ?: return@run failure(UserError.UserNotFound)
			success(user)
		}
	}

	override fun joinChannel(
		userId: UInt,
		channelId: UInt
	): Either<Error, Unit> {
		return repoManager.run {
			val user = userRepo.findById(userId) ?: return@run failure(UserError.UserNotFound)
			val channel = channelRepo.findById(channelId) ?: return@run failure(ChannelError.ChannelNotFound)
			user.uId?.let { uId -> channel.id?.let { cId -> userRepo.joinChannel(uId, cId) } }
			success(Unit)
		}
	}
}
