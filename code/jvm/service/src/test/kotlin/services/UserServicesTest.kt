package services

import TransactionManager
import errors.ChannelError
import errors.UserError
import errors.UserError.InvitationCodeMaxUsesReached
import jdbc.transactionManager.TransactionManagerJDBC
import mem.TransactionManagerInMem
import model.channels.AccessControl
import model.channels.Channel
import model.channels.ChannelInvitation
import model.channels.Visibility
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

    /**
     * Create a channel with an invitation
     * @param manager The transaction manager.
     * @param owner The owner of the channel
     * @return The channel created and the invitation code
     */
    private fun createChannelWithInvitation(
        manager: TransactionManager,
        owner: User,
        channelVisibility: String = Visibility.PUBLIC.name,
        invitationExpirationDate: String = Timestamp.valueOf(LocalDateTime.now().plusHours(1)).toString(),
        invitationAccessControl: String = AccessControl.READ_WRITE.name,
    ): Pair<Channel, UUID> {
        val channel = createChannelWithoutInvitation(manager, owner, channelVisibility)
        val invitationCode = UUID.randomUUID()
        val inviterId = checkNotNull(owner.uId)
        val invitationTimestamp = Timestamp.valueOf(invitationExpirationDate)
        val invitationUser = UserInvitation(inviterId, invitationTimestamp)
        val invitationChannel =
            ChannelInvitation(
                channelId = checkNotNull(channel.channelId),
                expirationDate = invitationTimestamp,
                maxUses = 1u,
                accessControl = AccessControl.valueOf(invitationAccessControl),
            )
        manager.run {
            userRepo.createInvitation(invitationUser)
            channelRepo.createInvitation(invitationChannel)
        }
        return Pair(channel, invitationCode)
    }

    private fun createChannelWithoutInvitation(
        manager: TransactionManager,
        owner: User,
        channelVisibility: String = Visibility.PUBLIC.name,
    ): Channel {
        val channelServices = ChannelServices(manager)
        val ownerId = checkNotNull(owner.uId) { "Owner id is null" }
        return channelServices
            .createChannel(ownerId, "name", AccessControl.READ_WRITE.name, channelVisibility)
            .let { (it as Success).value }
    }

    /**
     * Create a user with an invitation
     * @param manager The transaction manager.
     * @return The user created
     */
    private fun createUserWithInvitation(manager: TransactionManager): User {
        val userServices = UserServices(manager)
        val invitation = makeInviterAndInvitation(manager)
        val user =
            userServices
                .createUser(
                    "userToJoinChannel",
                    validPassword,
                    invitation.invitationCode.toString(),
                    invitation.inviterId,
                )
                as Success<User>
        return user.value
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `creating a new user with a valid invitation should return the User`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val invitation = makeInviterAndInvitation(manager)

        val newUser =
            userServices
                .createUser(usernameDefault2, validPassword, invitation.invitationCode.toString(), invitation.inviterId)

        assertIs<Success<User>>(newUser, "User creation failed with error" + (newUser as? Failure)?.value)
        assertNotNull(newUser.value.uId, "User id is null")
        assertEquals(usernameDefault2, newUser.value.username, "Username is different")
        assertEquals(passwordDefault, newUser.value.password, "Password is different")
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
                .createUser("", validPassword, invitation.invitationCode.toString(), invitation.inviterId)
        assertIs<Failure<UserError.UsernameIsEmpty>>(newUser)
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
                    invitation.inviterId,
                )
        assertIs<Failure<UserError.PasswordIsInvalid>>(newUser)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to create a user with an Invitation with an nonexistent inviter id`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val invitation = makeInviterAndInvitation(manager)
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
        manager: TransactionManager,
    ) {
        val userServices = UserServices(manager)
        val invitation = makeInviterAndInvitation(manager)
        val newUser =
            userServices
                .createUser(
                    usernameDefault1,
                    validPassword,
                    "0f7ed58e-89c0-4331-b22d-0d075b3563156",
                    invitation.inviterId,
                )
        assertIs<Failure<UserError.InvitationCodeIsInvalid>>(newUser)
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
                    usernameDefault1,
                    validPassword,
                    invitation.invitationCode.toString(),
                    invitation.inviterId,
                )
        assertIs<Failure<UserError.InvitationCodeHasExpired>>(newUser)
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
                    invitation.inviterId,
                )
        assertIs<Failure<UserError.UsernameAlreadyExists>>(newUser)
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
                    invitation.inviterId,
                )
                as Success<User>
        val userId = checkNotNull(user.value.uId)
        val result = userServices.deleteUser(userId)
        assertIs<Success<Unit>>(result)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to delete a nonexistent user should return UserNotFound`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val result = userServices.deleteUser(0u)
        assertIs<Failure<UserError.UserNotFound>>(result)
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
                    invitation.inviterId,
                )
                as Success<User>
        val userId = checkNotNull(user.value.uId)
        val result = userServices.getUser(userId)
        assertIs<Success<User>>(result)
        assertEquals(user.value, result.value)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to get a nonexistent user should return UserNotFound`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val result = userServices.getUser(0u)
        assertIs<Failure<UserError.UserNotFound>>(result)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `joining a Public channel successfully should return Unit`(manager: TransactionManager) {
        val owner = checkNotNull(ChannelServicesTest.makeUser(manager))
        val (channel, invitationCode) = createChannelWithInvitation(manager, owner)
        val user = createUserWithInvitation(manager)
        val userId = checkNotNull(user.uId)
        val userServices = UserServices(manager)
        val result = channel.channelId?.let { userServices.joinChannel(userId, it, invitationCode.toString()) }
        assertIs<Success<Unit>>(result)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `joining a Private channel successfully should return Unit`(manager: TransactionManager) {
        val owner = checkNotNull(ChannelServicesTest.makeUser(manager))
        val (channel, invitationCode) = createChannelWithInvitation(manager, owner, Visibility.PRIVATE.name)
        val user = createUserWithInvitation(manager)
        val userId = checkNotNull(user.uId)
        val userServices = UserServices(manager)
        val result = channel.channelId?.let { userServices.joinChannel(userId, it, invitationCode.toString()) }
        assertIs<Success<Unit>>(result)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to join a channel with a nonexistent channel should return ChannelNotFound`(
        manager: TransactionManager,
    ) {
        val user = createUserWithInvitation(manager)
        val userId = checkNotNull(user.uId)
        val userServices = UserServices(manager)
        val result = userServices.joinChannel(userId, 0u, "")
        assertIs<Failure<ChannelError.ChannelNotFound>>(result)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to join a channel with a nonexistent user should return UserNotFound`(manager: TransactionManager) {
        val owner = checkNotNull(ChannelServicesTest.makeUser(manager))
        val (channel, invitationCode) = createChannelWithInvitation(manager, owner)
        val userServices = UserServices(manager)
        val result = channel.channelId?.let { userServices.joinChannel(0u, it, invitationCode.toString()) }
        assertIs<Failure<UserError.UserNotFound>>(result)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to join a channel with a user that is already in the channel should return Unit`(
        manager: TransactionManager,
    ) {
        val owner = checkNotNull(ChannelServicesTest.makeUser(manager))
        val (channel, invitationCode) = createChannelWithInvitation(manager, owner)
        val user = createUserWithInvitation(manager)
        val userId = checkNotNull(user.uId)
        val userServices = UserServices(manager)
        val result = channel.channelId?.let { userServices.joinChannel(userId, it, invitationCode.toString()) }
        assertIs<Success<Unit>>(result)
        val result2 = channel.channelId?.let { userServices.joinChannel(userId, it, invitationCode.toString()) }
        assertIs<Success<Unit>>(result2)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `joining a public channel should return Unit`(manager: TransactionManager) {
        val owner = checkNotNull(ChannelServicesTest.makeUser(manager))
        val (channel, _) = createChannelWithInvitation(manager, owner, Visibility.PUBLIC.name)
        val user = createUserWithInvitation(manager)
        val userId = checkNotNull(user.uId)
        val userServices = UserServices(manager)
        val result = channel.channelId?.let { userServices.joinChannel(userId, it, "") }
        assertIs<Success<Unit>>(result)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to join a private channel without an invitation should return InvitationCodeIsInvalid`(
        manager: TransactionManager,
    ) {
        val owner = checkNotNull(ChannelServicesTest.makeUser(manager))
        val channel = createChannelWithoutInvitation(manager, owner, Visibility.PRIVATE.name)
        val user = createUserWithInvitation(manager)
        val userId = checkNotNull(user.uId)
        val userServices = UserServices(manager)
        val result = channel.channelId?.let { userServices.joinChannel(userId, it, "") }
        assertIs<Failure<UserError.InvitationCodeIsInvalid>>(result)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to join a channel with an expired invitation should return InvitationCodeHasExpired`(
        manager: TransactionManager,
    ) {
        val owner = checkNotNull(ChannelServicesTest.makeUser(manager))
        val (channel, invitationCode) =
            createChannelWithInvitation(
                manager = manager,
                owner = owner,
                channelVisibility = Visibility.PRIVATE.name,
                invitationExpirationDate = Timestamp.valueOf(LocalDateTime.now().minusDays(5)).toString(),
            )
        val user = createUserWithInvitation(manager)
        val userId = checkNotNull(user.uId)
        val userServices = UserServices(manager)
        val result = channel.channelId?.let { userServices.joinChannel(userId, it, invitationCode.toString()) }
        assertIs<Failure<UserError.InvitationCodeHasExpired>>(result)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `join a channel with invitation code that has reached the max uses should return InvitationCodeMaxUsesReached`(
        manager: TransactionManager,
    ) {
        val owner = checkNotNull(ChannelServicesTest.makeUser(manager))
        val (channel, invitationCode) =
            createChannelWithInvitation(
                manager = manager,
                owner = owner,
                channelVisibility = Visibility.PRIVATE.name,
            )
        val user = createUserWithInvitation(manager)
        val userId = checkNotNull(user.uId)
        val userServices = UserServices(manager)
        val result = channel.channelId?.let { userServices.joinChannel(userId, it, invitationCode.toString()) }
        assertIs<Success<Unit>>(result)
        val user2 = checkNotNull(ChannelServicesTest.makeUser(manager, "user2"))
        val user2Id = checkNotNull(user2.uId)
        val result2 = channel.channelId?.let { userServices.joinChannel(user2Id, it, invitationCode.toString()) }
        assertIs<Failure<InvitationCodeMaxUsesReached>>(result2)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun isValidTokenTest(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val user = checkNotNull(ChannelServicesTest.makeUser(manager))
        val token = userServices.login(user.username, validPassword) as Success<UserToken>
        val result = userServices.isValidToken(token.value.token.toString())
        val result2 = userServices.isValidToken("0f7ed58e-89c0-4331-b22d-0d075b356317")
        assertIs<Success<Boolean>>(result)
        assertEquals(true, result.value)
        assertIs<Success<Boolean>>(result2)
        assertEquals(false, result2.value)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `getting an invitation by its inviter id and invitation code should return the invitation`(
        manager: TransactionManager,
    ) {
        val userServices = UserServices(manager)
        val invitation =
            makeInviterAndInvitation(
                manager,
            )
        val result =
            userServices.getInvitation(
                invitation.inviterId,
                invitation.invitationCode.toString(),
            )
        assertIs<Success<UserInvitation>>(result)
        assertEquals(invitation.inviterId, result.value.inviterId)
        assertEquals(invitation.invitationCode, result.value.invitationCode)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to get an invitation with an invalid inviter id should return InviterNotFound`(
        manager: TransactionManager,
    ) {
        val userServices = UserServices(manager)
        val invitation =
            makeInviterAndInvitation(
                manager,
            )
        val result =
            userServices.getInvitation(
                0u,
                invitation.invitationCode.toString(),
            )
        assertIs<Failure<UserError.InviterNotFound>>(result)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to get an invitation with an invalid invitation code should return InvitationNotFound`(
        manager: TransactionManager,
    ) {
        val userServices = UserServices(manager)
        val invitation =
            makeInviterAndInvitation(
                manager,
            )
        val result =
            userServices.getInvitation(
                invitation.inviterId,
                "0f7ed58e-89c0-4331-b22d-0d075b356317",
            )
        assertIs<Failure<UserError.InvitationNotFound>>(result)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `successfully logging in should return a valid UserToken`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val user = checkNotNull(ChannelServicesTest.makeUser(manager))
        val result = userServices.login(user.username, validPassword)
        assertIs<Success<UserToken>>(result)
        assertEquals(user.uId, result.value.userId)
        assertNotNull(result.value.token)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to login with an invalid username should return UserNotFound`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val result = userServices.login("invalid_username", validPassword)
        assertIs<Failure<UserError.UserNotFound>>(result)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to login with an invalid password should return PasswordIsInvalid`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val user = checkNotNull(ChannelServicesTest.makeUser(manager))
        val result = userServices.login(user.username, invalidPassword)
        assertIs<Failure<UserError.PasswordIsInvalid>>(result)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `logging out a user successfully should return Unit`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val user = checkNotNull(ChannelServicesTest.makeUser(manager))
        val token = userServices.login(user.username, validPassword) as Success<UserToken>
        val result = userServices.logout(token.value.token.toString())
        assertIs<Success<Unit>>(result)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to logout with a nonexistent token should return TokenNotFound`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val result = userServices.deleteUser(0u)
        assertIs<Failure<UserError.UserNotFound>>(result)
    }
}