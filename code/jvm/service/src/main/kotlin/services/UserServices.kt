package services

import TransactionManager
import errors.UserError
import errors.UserError.InvitationCodeHasExpired
import errors.UserError.InvitationCodeIsInvalid
import interfaces.UserServicesInterface
import jakarta.inject.Named
import model.users.Password
import model.users.User
import model.users.UserInvitation
import model.users.UserToken
import utils.Either
import utils.failure
import utils.success
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * The number of days a token is valid.
 */
private const val TOKEN_EXPIRATION_DAYS = 7L

/**
 * The services available for the user entity.
 * @property repoManager The transaction manager.
 */
@Named("UserServices")
class UserServices(
    private val repoManager: TransactionManager,
) : UserServicesInterface {
    override fun createUser(
        username: String,
        password: String,
        invitationCode: String,
    ): Either<UserError, User> {
        if (username.isEmpty()) return failure(UserError.UsernameIsEmpty)
        if (!Password.isValidPassword(password)) return failure(UserError.PasswordIsInvalid)
        val user =
            User(
                username = username,
                password = Password(password),
            )
        return repoManager.run {
            if (userRepo.findByUsername(username) != null) return@run failure(UserError.UsernameAlreadyExists)
            val invitation =
                userRepo
                    .findInvitation(invitationCode)
                    ?: return@run failure(InvitationCodeIsInvalid)
            if (invitation.isExpired) {
                userRepo.deleteInvitation(invitation)
                return@run failure(InvitationCodeHasExpired)
            }
            val createdUser = userRepo.createUser(user) ?: return@run failure(UserError.UnableToCreateUser)
            userRepo.deleteInvitation(invitation)
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

    override fun getInvitation(invitationCode: String): Either<UserError, UserInvitation> {
        return repoManager.run {
            val invitation =
                userRepo
                    .findInvitation(invitationCode) ?: return@run failure(UserError.InvitationNotFound)
            success(invitation)
        }
    }

    override fun login(
        username: String,
        password: String,
    ): Either<UserError, UserToken> {
        return repoManager.run {
            val user = userRepo.findByUsername(username) ?: return@run failure(UserError.UserNotFound)
            if (!user.password.matches(password)) return@run failure(UserError.PasswordIsInvalid)
            val userId = checkNotNull(user.uId)
            val token =
                UserToken(
                    uId = userId,
                    expirationDate = Timestamp.valueOf(LocalDateTime.now().plusDays(TOKEN_EXPIRATION_DAYS)),
                )
            if (userRepo.createToken(token)) success(token) else failure(UserError.UnableToCreateToken)
        }
    }

    override fun logout(
        token: String,
        uId: UInt,
    ): Either<UserError, Unit> =
        repoManager.run {
            if (!userRepo.validateToken(token)) return@run failure(UserError.TokenNotFound)
            if (userRepo.findById(uId) == null) return@run failure(UserError.UserNotFound)
            if (!userRepo.deleteToken(token)) return@run failure(UserError.UnableToDeleteToken)
            success(Unit)
        }

    override fun createInvitation(
        inviterUId: UInt,
        expirationDate: String?,
        ): Either<UserError, UserInvitation> {
        val timestamp =
            if (expirationDate != null) {
                makeTimeStamp(expirationDate) ?: return failure(UserError.UnableToCreateInvitation)
            } else {
                LocalDateTime
                .now()
                .plusWeeks(1)
                .let(Timestamp::valueOf)
    }
        return repoManager.run {
            userRepo.findById(inviterUId) ?: return@run failure(UserError.InviterNotFound)
            val invitation =
                UserInvitation(
                    inviterId = inviterUId,
                    expirationDate = timestamp,
                )
            if (userRepo.createInvitation(invitation)) {
                success(invitation)
            } else {
                failure(UserError.UnableToCreateInvitation)
            }
        }
    }
    private fun makeTimeStamp(expirationDate: String) =
        try {
            Timestamp.valueOf(
                LocalDateTime
                    .parse(expirationDate),
            )
        } catch (e: IllegalArgumentException) {
            null
        }
}