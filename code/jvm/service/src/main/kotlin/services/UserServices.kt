package services

import TransactionManager
import errors.ChannelError.ChannelNotFound
import errors.ChannelError.InvitationCodeHasExpired
import errors.ChannelError.InvitationCodeIsInvalid
import errors.ChannelError.InvitationCodeMaxUsesReached
import errors.ChannelError.UserNotFound
import errors.Error
import errors.UserError
import interfaces.UserServicesInterface
import jakarta.inject.Inject
import jakarta.inject.Named
import model.channels.Channel
import model.channels.decrementUses
import model.users.Password
import model.users.User
import utils.Either
import utils.failure
import utils.success

@Named("UserServices")
class UserServices
    @Inject
    constructor(
        @Named("TransactionManagerJDBC") private val repoManager: TransactionManager,
    ) : UserServicesInterface {
        override fun createUser(
            username: String,
            password: String,
            invitationCode: String,
            inviterUId: UInt,
        ): Either<UserError, User> {
            if (username.isEmpty()) return failure(UserError.UsernameIsEmpty)
            if (!Password.isValidPassword(password)) return failure(UserError.PasswordIsInvalid)
            val user =
                User(
                    username = username,
                    password = Password(password),
                )
            return repoManager.run {
                userRepo.findById(inviterUId) ?: return@run failure(UserError.InviterNotFound)
                val invitation =
                    userRepo
                        .findInvitation(inviterUId, invitationCode)
                        ?: return@run failure(UserError.InvitationCodeIsInvalid)
                if (invitation.isExpired) {
                    userRepo.deleteInvitation(invitation)
                    return@run failure(UserError.InvitationCodeHasExpired)
                }
                val createdUser = userRepo.createUser(user) ?: return@run failure(UserError.UserAlreadyExists)
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
            invitationCode: String,
        ): Either<Error, Unit> {
            return repoManager.run {
                val channel =
                    channelRepo.findById(channelId) ?: return@run failure(ChannelNotFound)
                userRepo.findById(userId) ?: return@run failure(UserNotFound)
                if (channelRepo.isUserInChannel(channelId, userId)) {
                    return@run success(Unit)
                }
                if (channel is Channel.Public) {
                    userRepo.joinChannel(channelId, userId, channel.accessControl)
                    return@run success(Unit)
                }
                val invitation =
                    channelRepo.findInvitation(channelId) ?: return@run failure(InvitationCodeIsInvalid)
                if (invitation.isExpired) {
                    channelRepo.deleteInvitation(channelId)
                    return@run failure(InvitationCodeHasExpired)
                }
                if (invitation.maxUses == 0u) {
                    channelRepo.deleteInvitation(channelId)
                    return@run failure(InvitationCodeMaxUsesReached)
                }
                channelRepo.updateInvitation(invitation.decrementUses())
                userRepo.joinChannel(channelId, userId, invitation.accessControl)
                success(Unit)
            }
        }
    }
