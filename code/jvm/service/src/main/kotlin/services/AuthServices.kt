package services

import TransactionManager
import errors.UserError
import interfaces.AuthServiceInterface
import jakarta.inject.Named
import model.users.UserToken
import utils.Either
import utils.failure
import utils.success

@Named("AuthServices")
class AuthServices(
    private val repoManager: TransactionManager,
) : AuthServiceInterface {
    override fun getUserByToken(token: String): Either<UserError, UserToken> =
        repoManager.run {
            val user = userRepo.findToken(token) ?: return@run failure(UserError.UserNotFound)
            success(user)
        }
}