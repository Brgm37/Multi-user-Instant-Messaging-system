package services

import TransactionManager
import errors.UserError
import jdbc.transactionManager.TransactionManagerJDBC
import mem.TransactionManagerInMem
import model.users.Password
import model.users.User
import model.users.UserInvitation
import model.users.UserToken
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import utils.Failure
import utils.Success
import utils.encryption.DummyEncrypt
import java.sql.Timestamp
import java.time.LocalDateTime
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

    companion object {
        @JvmStatic
        fun transactionManagers(): Stream<TransactionManager> =
            Stream.of(
                TransactionManagerInMem().also { cleanup(it) },
                TransactionManagerJDBC(TestSetup.dataSource, DummyEncrypt).also { cleanup(it) },
            )

        private fun cleanup(manager: TransactionManager) =
            manager.run {
                channelRepo.clear()
                messageRepo.clear()
                userRepo.clear()
            }
    }

    /**
     * Create an inviter and an invitation
     * @param manager The transaction manager.
     * @param expirationDate the expirationDare of the invitation to be created
     * @return The invitation created
     */
    private fun makeInviterAndInvitation(
        manager: TransactionManager,
        username: String = usernameDefault1,
        password: Password = passwordDefault,
        expirationDate: Timestamp = Timestamp.valueOf(LocalDateTime.now().plusHours(1)),
    ) = manager.run {
        val inviter = userRepo.createUser(User(username = username, password = password))
        val inviterUId = checkNotNull(inviter?.uId)
        val invitation = UserInvitation(inviterUId, expirationDate)
        userRepo.createInvitation(invitation)
        return@run invitation
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `creating a new user with a valid invitation should return the User`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val invitation = makeInviterAndInvitation(manager)

        val newUser =
            userServices
                .createUser(usernameDefault2, validPassword, invitation.invitationCode.toString())

        assertIs<Success<UserToken>>(newUser, "User creation failed with error" + (newUser as? Failure)?.value)
        assertNotNull(newUser.value.uId, "User id is null")
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to create a user with an empty username should return UsernameIsEmptyError`(
        manager: TransactionManager,
    ) {
        val userServices = UserServices(manager)
        val invitation = makeInviterAndInvitation(manager)
        val newUser =
            userServices
                .createUser("", validPassword, invitation.invitationCode.toString())
        assertIs<Failure<UserError.UsernameIsEmpty>>(newUser)
        assertEquals(newUser.value, UserError.UsernameIsEmpty)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to create a user with an invalid password should return PasswordIsInvalid`(
        manager: TransactionManager,
    ) {
        val userServices = UserServices(manager)
        val invitation = makeInviterAndInvitation(manager)
        val newUser =
            userServices
                .createUser(
                    usernameDefault1,
                    invalidPassword,
                    invitation.invitationCode.toString(),
                )
        assertIs<Failure<UserError.PasswordIsInvalid>>(newUser)
        assertEquals(newUser.value, UserError.PasswordIsInvalid)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to create a user with and invitation with invalid code should return InvitationCodeIsInvalid`(
        manager: TransactionManager,
    ) {
        val userServices = UserServices(manager)
        val newUser =
            userServices
                .createUser(
                    "newUser",
                    validPassword,
                    "invalid",
                )
        assertIs<Failure<UserError.InvitationCodeIsInvalid>>(newUser)
        assertEquals(newUser.value, UserError.InvitationCodeIsInvalid)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to create a user with an expired invitation should return InvitationCodeHasExpired`(
        manager: TransactionManager,
    ) {
        val userServices = UserServices(manager)
        val expirationDate = Timestamp.valueOf(LocalDateTime.now().minusHours(1))
        val invitation = makeInviterAndInvitation(manager, expirationDate = expirationDate)
        val newUser =
            userServices
                .createUser(
                    "newUser",
                    validPassword,
                    invitation.invitationCode.toString(),
                )
        assertIs<Failure<UserError.InvitationCodeHasExpired>>(newUser)
        assertEquals(newUser.value, UserError.InvitationCodeHasExpired)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to create a user with a username that already exists should return UsernameAlreadyExists`(
        manager: TransactionManager,
    ) {
        val userServices = UserServices(manager)
        val invitation = makeInviterAndInvitation(manager)
        val newUser =
            userServices
                .createUser(
                    usernameDefault1,
                    validPassword,
                    invitation.invitationCode.toString(),
                )
        assertIs<Failure<UserError.UsernameAlreadyExists>>(newUser)
        assertEquals(newUser.value, UserError.UsernameAlreadyExists)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `deleting a user successfully should return Unit`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val invitation = makeInviterAndInvitation(manager)
        val user =
            userServices
                .createUser(
                    "userToDelete",
                    validPassword,
                    invitation.invitationCode.toString(),
                )
                as Success<UserToken>
        val userId = checkNotNull(user.value.uId)
        val result = userServices.deleteUser(userId)
        assertIs<Success<Unit>>(result)
        assertEquals(Unit, result.value)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to delete a nonexistent user should return UserNotFound`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val result = userServices.deleteUser(0u)
        assertIs<Failure<UserError.UserNotFound>>(result)
        assertEquals(UserError.UserNotFound, result.value)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `getting a user by its id should return the user`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val invitation = makeInviterAndInvitation(manager)
        val user =
            userServices
                .createUser(
                    "userToGet",
                    validPassword,
                    invitation.invitationCode.toString(),
                )
                as Success<UserToken>
        val userId = checkNotNull(user.value.uId)
        val result = userServices.getUser(userId)
        assertIs<Success<User>>(result)
        assertEquals(user.value.uId, result.value.uId)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to get a nonexistent user should return UserNotFound`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val result = userServices.getUser(0u)
        assertIs<Failure<UserError.UserNotFound>>(result)
        assertEquals(UserError.UserNotFound, result.value)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `getting an invitation by valid invitation code should return the invitation`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val invitation = makeInviterAndInvitation(manager)
        val result = userServices.getInvitation(invitation.invitationCode.toString())
        assertIs<Success<UserInvitation>>(result)
        assertEquals(invitation.inviterId, result.value.inviterId)
        assertEquals(invitation.invitationCode, result.value.invitationCode)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to get an invitation with an invalid invitation code should return InvitationNotFound`(
        manager: TransactionManager,
    ) {
        val userServices = UserServices(manager)
        val result = userServices.getInvitation("b4e4d6cd-6f11-44c0-9c93-3b1c2f2d1987")
        assertIs<Failure<UserError.InvitationNotFound>>(result)
        assertEquals(UserError.InvitationNotFound, result.value)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `successfully logging in should return a valid UserToken`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val user = checkNotNull(ChannelServicesTest.makeUser(manager))
        val result = userServices.login(user.username, validPassword)
        assertIs<Success<UserToken>>(result)
        assertEquals(user.uId, result.value.uId)
        assertNotNull(result.value.token)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to login with an invalid username should return UserNotFound`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val result = userServices.login("invalid_username", validPassword)
        assertIs<Failure<UserError.UserNotFound>>(result)
        assertEquals(UserError.UserNotFound, result.value)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to login with an invalid password should return PasswordIsInvalid`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val user = checkNotNull(ChannelServicesTest.makeUser(manager))
        val result = userServices.login(user.username, invalidPassword)
        assertIs<Failure<UserError.PasswordIsInvalid>>(result)
        assertEquals(UserError.PasswordIsInvalid, result.value)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `logging out a user successfully should return Unit`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val user = checkNotNull(ChannelServicesTest.makeUser(manager))
        val token = userServices.login(user.username, validPassword) as Success<UserToken>
        val result = userServices.logout(token.value.token.toString(), checkNotNull(user.uId))
        assertIs<Success<Unit>>(result)
        assertEquals(Unit, result.value)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to logout with a nonexistent token should return TokenNotFound`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val user = checkNotNull(ChannelServicesTest.makeUser(manager))
        val result = userServices.logout("nonexistent_token", checkNotNull(user.uId))
        assertIs<Failure<UserError.TokenNotFound>>(result)
        assertEquals(UserError.TokenNotFound, result.value)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to logout with a nonexistent user should return UserNotFound`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val user = checkNotNull(ChannelServicesTest.makeUser(manager))
        val token = userServices.login(user.username, validPassword) as Success<UserToken>
        val result = userServices.logout(token.value.token.toString(), 0u)
        assertIs<Failure<UserError.UserNotFound>>(result)
        assertEquals(UserError.UserNotFound, result.value)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `creating an invitation should return the invitation`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val inviter = checkNotNull(ChannelServicesTest.makeUser(manager))
        val inviterId = checkNotNull(inviter.uId)
        val result = userServices.createInvitation(inviterId, null)
        assertIs<Success<UserInvitation>>(result)
        assertEquals(inviter.uId, result.value.inviterId)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to create an invitation with an invalid inviter id should return InviterNotFound`(
        manager: TransactionManager,
    ) {
        val userServices = UserServices(manager)
        val result = userServices.createInvitation(0u, null)
        assertIs<Failure<UserError.InviterNotFound>>(result)
        assertEquals(UserError.InviterNotFound, result.value)
    }
}