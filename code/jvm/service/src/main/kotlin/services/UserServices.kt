package services

import TransactionManager
import errors.ChannelError.ChannelNotFound
import errors.Error
import errors.UserError
import errors.UserError.InvitationCodeHasExpired
import errors.UserError.InvitationCodeIsInvalid
import errors.UserError.InvitationCodeMaxUsesReached
import interfaces.UserServicesInterface
import jakarta.inject.Named
import model.channels.Channel
import model.channels.decrementUses
import model.users.Password
import model.users.User
import model.users.UserInvitation
import model.users.UserToken
import utils.Either
import utils.failure
import utils.success
import java.sql.Timestamp
import java.time.LocalDateTime

/**
 * The number of days an invitation is valid.
 */
private const val INVITATION_EXPIRATION_DAYS = 7L

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

    override fun joinChannel(
        userId: UInt,
        channelId: UInt,
        invitationCode: String?,
    ): Either<Error, Unit> {
        return repoManager.run {
            val channel =
                channelRepo.findById(channelId) ?: return@run failure(ChannelNotFound)
            userRepo.findById(userId) ?: return@run failure(UserError.UserNotFound)
            if (channelRepo.isUserInChannel(channelId, userId)) {
                return@run success(Unit)
            }
            if (channel is Channel.Public) {
                channelRepo.joinChannel(channelId, userId, channel.accessControl)
                return@run success(Unit)
            }
            val invitation = channelRepo.findInvitation(channelId) ?: return@run failure(InvitationCodeIsInvalid)
            if (invitationCode != invitation.invitationCode.toString()) {
                return@run failure(InvitationCodeIsInvalid)
            }
            if (invitation.isExpired) {
                channelRepo.deleteInvitation(channelId)
                return@run failure(InvitationCodeHasExpired)
            }
            if (invitation.maxUses == 0u) {
                channelRepo.deleteInvitation(channelId)
                return@run failure(InvitationCodeMaxUsesReached)
            }
            channelRepo.updateInvitation(invitation.decrementUses())
            channelRepo.joinChannel(channelId, userId, invitation.accessControl)
            success(Unit)
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
                    userId = userId,
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

    override fun createInvitation(inviterUId: UInt): Either<UserError, UserInvitation> {
        return repoManager.run {
            userRepo.findById(inviterUId) ?: return@run failure(UserError.InviterNotFound)
            val invitation =
                UserInvitation(
                    inviterId = inviterUId,
                    expirationDate = Timestamp.valueOf(LocalDateTime.now().plusDays(INVITATION_EXPIRATION_DAYS)),
                )
            if (userRepo.createInvitation(invitation)) {
                success(invitation)
            } else {
                failure(UserError.UnableToCreateInvitation)
            }
        }
    }
}