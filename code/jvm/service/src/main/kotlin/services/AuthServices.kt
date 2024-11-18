package services

import TransactionManager
import errors.UserError
import interfaces.AuthServiceInterface
import jakarta.inject.Named
import model.users.User
import utils.Either
import utils.failure
import utils.success

@Named("AuthServices")
class AuthServices(
    private val repoManager: TransactionManager,
) : AuthServiceInterface {
    override fun getUserByToken(token: String): Either<UserError, User> =
        repoManager.run {
            val user = userRepo.findByToken(token) ?: return@run failure(UserError.UserNotFound)
            success(user)
        }
}