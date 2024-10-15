package services

import TransactionManager
import errors.UserError
import jdbc.transactionManager.TransactionManagerJDBC
import mem.TransactionManagerInMem
import model.users.Password
import model.users.User
import model.users.UserInvitation
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import utils.Failure
import utils.Success
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.UUID
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class UserServicesTest {
    private val validPassword = "Password123"
    private val invalidPassword = "invalid_password"
    private val passwordDefault = Password(validPassword)
    private val usernameDefault1 = "name"
    private val usernameDefault2 = "name2"
    private val uIdDefault = 1u
    private val cIdDefault = 1u
    private val userDefault = User(null, usernameDefault1, passwordDefault)
    private val userWithIdDefault = User(uIdDefault, usernameDefault1, passwordDefault)

    companion object {
        @JvmStatic
        fun transactionManagers(): Stream<TransactionManager> =
            Stream.of(
                TransactionManagerInMem().also { cleanup(it) },
                TransactionManagerJDBC(Environment).also { cleanup(it) },
            )

        private fun cleanup(manager: TransactionManager) =
            manager.run {
                userRepo.clear()
                channelRepo.clear()
                messageRepo.clear()
            }
    }

    /**
     * Create an inviter and an invitation
     * @param manager The transaction manager.
     * @param expirationDate the expirationDare of the invitation to be created
     * @return The invitation created
     */
    private fun makeInvitation(
        manager: TransactionManager,
        expirationDate: Timestamp = Timestamp.valueOf(LocalDateTime.now().plusHours(1))
    ) =
        manager.run {
            val inviter = userRepo.createUser(userDefault)
            val inviterUId = checkNotNull(inviter?.uId)
            val invitation = UserInvitation(
                inviterUId,
                expirationDate,
            )
            userRepo.createInvitation(
                invitation,
            )
            return@run invitation
        }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `creating a new user with a valid invitation should return the User`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val invitation = makeInvitation(manager)

        val newUser =
            userServices
                .createUser(usernameDefault2, validPassword, invitation.invitationCode.toString(), invitation.userId)

        assertIs<Success<User>>(newUser, "User creation failed with error" + (newUser as? Failure)?.value)
        assertNotNull(newUser.value.uId, "User id is null")
        assertEquals(usernameDefault2, newUser.value.username, "Username is different")
        assertEquals(passwordDefault, newUser.value.password, "Password is different")
        assertIs<UUID>(newUser.value.token, "Token is not a UUID")
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to create a user with an empty username should return UsernameIsEmptyError`(
        manager: TransactionManager
    ) {
        val userServices = UserServices(manager)
        val invitation = makeInvitation(manager)
        val newUser =
            userServices
                .createUser("", validPassword, invitation.invitationCode.toString(), invitation.userId)
        assertIs<Failure<UserError.UsernameIsEmpty>>(newUser)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to create a user with an invalid password should return PasswordIsInvalid`(
        manager: TransactionManager
    ) {
        val userServices = UserServices(manager)
        val invitation = makeInvitation(manager)
        val newUser =
            userServices
                .createUser(usernameDefault1, invalidPassword, invitation.invitationCode.toString(), invitation.userId)
        assertIs<Failure<UserError.PasswordIsInvalid>>(newUser)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to create a user with an Invitation with an nonexistent inviter id`(
        manager: TransactionManager
    ) {
        val userServices = UserServices(manager)
        val invitation = makeInvitation(manager)
        val newUser =
            userServices
                .createUser(
                    usernameDefault1,
                    invalidPassword,
                    invitation.invitationCode.toString(),
                    0u,
                )
        assertIs<Failure<UserError.InviterNotFound>>(newUser)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to create a user with and invitation with invalid code should return InvitationCodeIsInvalid`(
        manager: TransactionManager
    ) {
        val userServices = UserServices(manager)
        val invitation = makeInvitation(manager)
        val newUser =
            userServices
                .createUser(
                    usernameDefault1,
                    invalidPassword,
                    "0f7ed58e-89c0-4331-b22d-0d075b3563156",
                    invitation.userId,
                )
        assertIs<Failure<UserError.InvitationCodeIsInvalid>>(newUser)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to create a user with an expired invitation should return InvitationCodeHasExpired`(
        manager: TransactionManager
    ) {
        val userServices = UserServices(manager)
        val expirationDate = Timestamp.valueOf(LocalDateTime.now().minusHours(1))
        val invitation = makeInvitation(manager, expirationDate)
        val newUser =
            userServices
                .createUser(
                    usernameDefault1,
                    validPassword,
                    invitation.invitationCode.toString(),
                    invitation.userId,
                )
        assertIs<Failure<UserError.InvitationCodeHasExpired>>(newUser)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to create a user with a username that already exists should return UsernameAlreadyExists`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val invitation = makeInvitation(manager)
        val user =
            userServices
                .createUser(usernameDefault2, validPassword, invitation.invitationCode.toString(), invitation.userId)
                as Success<User>
        val invitation2 = manager
            .run {
                val inviter = userRepo.createUser(
                    User(
                        username = "inviter2",
                        password = passwordDefault,
                    ),
                )
                val inviterUId = checkNotNull(inviter?.uId)
                val invitation2 = UserInvitation(
                    inviterUId,
                    Timestamp.valueOf(LocalDateTime.now().plusHours(1)),
                )
                userRepo.createInvitation(
                    invitation2,
                )
                return@run invitation2
            }
        val newUser =
            userServices
                .createUser(
                    user.value.username,
                    validPassword,
                    invitation2.invitationCode.toString(),
                    invitation2.userId,
                )
        assertIs<Failure<UserError.UsernameAlreadyExists>>(newUser)
    }
}
