package services

import TransactionManager
import errors.ChannelError
import errors.UserError
import jdbc.transactionManager.TransactionManagerJDBC
import mem.TransactionManagerInMem
import model.channels.AccessControl
import model.channels.Channel
import model.channels.ChannelInvitation
import model.channels.ChannelName
import model.users.Password
import model.users.User
import model.users.UserInfo
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
                )
                as Success<User>
        return user.value
    }

    /**
     * Create a public channel with an invitation
     * @param manager The transaction manager.
     * @param owner The owner of the channel
     * @return The channel and the invitation code
     */
    private fun createChannelPublic(
        manager: TransactionManager,
        owner: User,
    ) = manager.run {
        val ownerId = checkNotNull(owner.uId)
        val channel =
            Channel.Public(
                owner = UserInfo(ownerId, owner.username),
                name = ChannelName("channel", owner.username),
                accessControl = AccessControl.READ_WRITE,
            )
        return@run channelRepo.createChannel(channel)
    }

    /**
     * Create a private channel with an invitation
     * @param manager The transaction manager.
     * @param owner The owner of the channel
     * @param expirationDate The expiration date of the invitation
     * @return a Private channel and a ChannelInvitation to join that channel
     */
    private fun createPrivateChannel(
        manager: TransactionManager,
        owner: User,
        expirationDate: Timestamp = Timestamp.valueOf(LocalDateTime.now().plusHours(1)),
    ) = manager.run {
        val ownerId = checkNotNull(owner.uId)
        val privateChannel =
            Channel.Private(
                owner = UserInfo(ownerId, owner.username),
                name = ChannelName("channel", owner.username),
                accessControl = AccessControl.READ_WRITE,
            )
        val channel = checkNotNull(channelRepo.createChannel(privateChannel))
        val invitation =
            ChannelInvitation(
                cId = checkNotNull(channel.cId),
                expirationDate = expirationDate,
                maxUses = 1u,
                accessControl = AccessControl.READ_WRITE,
            )
        channelRepo.createInvitation(invitation)
        return@run Pair(channel, invitation)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `creating a new user with a valid invitation should return the User`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val invitation = makeInviterAndInvitation(manager)

        val newUser =
            userServices
                .createUser(usernameDefault2, validPassword, invitation.invitationCode.toString())

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
                as Success<User>
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
        assertEquals(UserError.UserNotFound, result.value)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `joining a Public channel successfully should return Unit`(manager: TransactionManager) {
        val owner = checkNotNull(ChannelServicesTest.makeUser(manager))
        val channel = checkNotNull(createChannelPublic(manager, owner))
        val cId = checkNotNull(channel.cId)
        val user = createUserWithInvitation(manager)
        val userId = checkNotNull(user.uId)
        val result = UserServices(manager).joinChannel(userId, cId, null)
        assertIs<Success<Unit>>(result)
        assertEquals(Unit, result.value)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `joining a Private channel successfully should return Unit`(manager: TransactionManager) {
        val owner = checkNotNull(ChannelServicesTest.makeUser(manager))
        val (channel, invitationCode) = createPrivateChannel(manager, owner)
        val user = createUserWithInvitation(manager)
        val userId = checkNotNull(user.uId)
        val userServices = UserServices(manager)
        val result =
            channel.cId?.let { userServices.joinChannel(userId, it, invitationCode.invitationCode.toString()) }
        assertIs<Success<Unit>>(result)
        assertEquals(Unit, result.value)
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
        assertEquals(ChannelError.ChannelNotFound, result.value)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to join a channel with a nonexistent user should return UserNotFound`(manager: TransactionManager) {
        val owner = checkNotNull(ChannelServicesTest.makeUser(manager))
        val channel = checkNotNull(createChannelPublic(manager, owner))
        val userServices = UserServices(manager)
        val result = channel.cId?.let { userServices.joinChannel(0u, it, null) }
        assertIs<Failure<UserError.UserNotFound>>(result)
        assertEquals(UserError.UserNotFound, result.value)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to join a channel with a user that is already in the channel should return Unit`(
        manager: TransactionManager,
    ) {
        val owner = checkNotNull(ChannelServicesTest.makeUser(manager))
        val channel = checkNotNull(createChannelPublic(manager, owner))
        val user = createUserWithInvitation(manager)
        val userId = checkNotNull(user.uId)
        val userServices = UserServices(manager)
        val result = channel.cId?.let { userServices.joinChannel(userId, it, null) }
        assertIs<Success<Unit>>(result)
        assertEquals(Unit, result.value)
        val result2 = channel.cId?.let { userServices.joinChannel(userId, it, null) }
        assertIs<Success<Unit>>(result2)
        assertEquals(Unit, result2.value)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `joining a public channel should return Unit`(manager: TransactionManager) {
        val owner = checkNotNull(ChannelServicesTest.makeUser(manager))
        val channel = checkNotNull(createChannelPublic(manager, owner))
        val user = createUserWithInvitation(manager)
        val userId = checkNotNull(user.uId)
        val userServices = UserServices(manager)
        val result = channel.cId?.let { userServices.joinChannel(userId, it, null) }
        assertIs<Success<Unit>>(result)
        assertEquals(Unit, result.value)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to join a private channel without an invitation should return InvitationCodeIsInvalid`(
        manager: TransactionManager,
    ) {
        val owner = checkNotNull(ChannelServicesTest.makeUser(manager))
        val (channel, _) = createPrivateChannel(manager, owner)
        val user = createUserWithInvitation(manager)
        val userId = checkNotNull(user.uId)
        val userServices = UserServices(manager)
        val result = channel.cId?.let { userServices.joinChannel(userId, it, null) }
        assertIs<Failure<UserError.InvitationCodeIsInvalid>>(result)
        assertEquals(UserError.InvitationCodeIsInvalid, result.value)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to join a channel with an expired invitation should return InvitationCodeHasExpired`(
        manager: TransactionManager,
    ) {
        val owner = checkNotNull(ChannelServicesTest.makeUser(manager))
        val (channel, invitationCode) =
            createPrivateChannel(
                manager = manager,
                owner = owner,
                expirationDate = Timestamp.valueOf(LocalDateTime.now().minusDays(5)),
            )
        val user = createUserWithInvitation(manager)
        val userId = checkNotNull(user.uId)
        val userServices = UserServices(manager)
        val result =
            channel.cId?.let { userServices.joinChannel(userId, it, invitationCode.invitationCode.toString()) }
        assertIs<Failure<UserError.InvitationCodeHasExpired>>(result)
        assertEquals(UserError.InvitationCodeHasExpired, result.value)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `join a channel with invitation code that has reached the max uses should return InvitationCodeMaxUsesReached`(
        manager: TransactionManager,
    ) {
        val owner = checkNotNull(ChannelServicesTest.makeUser(manager))
        val (channel, invitationCode) =
            createPrivateChannel(
                manager = manager,
                owner = owner,
            )
        val user = createUserWithInvitation(manager)
        val userId = checkNotNull(user.uId)
        val userServices = UserServices(manager)
        val result =
            channel.cId?.let { userServices.joinChannel(userId, it, invitationCode.invitationCode.toString()) }
        assertIs<Success<Unit>>(result)
        val user2 = checkNotNull(ChannelServicesTest.makeUser(manager, "user2"))
        val user2Id = checkNotNull(user2.uId)
        val result2 =
            channel.cId?.let { userServices.joinChannel(user2Id, it, invitationCode.invitationCode.toString()) }
        assertIs<Failure<UserError.InvitationCodeMaxUsesReached>>(result2)
        assertEquals(UserError.InvitationCodeMaxUsesReached, result2.value)
    }

//    @ParameterizedTest
//    @MethodSource("transactionManagers")
//    fun isValidTokenTest(manager: TransactionManager) {
//        val userServices = UserServices(manager)
//        val user = checkNotNull(ChannelServicesTest.makeUser(manager))
//        val token = userServices.login(user.username, validPassword) as Success<UserToken>
//        val result = userServices.isValidToken(token.value.token.toString())
//        val result2 = userServices.isValidToken("0f7ed58e-89c0-4331-b22d-0d075b356317")
//        assertIs<Success<Boolean>>(result)
//        assertEquals(true, result.value)
//        assertIs<Success<Boolean>>(result2)
//        assertEquals(false, result2.value)
//    }

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
        assertEquals(user.uId, result.value.userId)
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

//    @ParameterizedTest
//    @MethodSource("transactionManagers")
//    fun `getting a user by token should return the user`(manager: TransactionManager) {
//        val userServices = UserServices(manager)
//        val user = checkNotNull(ChannelServicesTest.makeUser(manager))
//        val token = userServices.login(user.username, validPassword) as Success<UserToken>
//        val result = userServices.getUserByToken(token.value.token.toString())
//        assertIs<Success<User>>(result)
//        assertEquals(user, result.value)
//    }
//
//    @ParameterizedTest
//    @MethodSource("transactionManagers")
//    fun `trying to get a user by token that does not exist should return UserNotFound`(manager: TransactionManager) {
//        val userServices = UserServices(manager)
//        val result = userServices.getUserByToken("nonexistent_token")
//        assertIs<Failure<UserError.UserNotFound>>(result)
//        assertEquals(UserError.UserNotFound, result.value)
//    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `creating an invitation should return the invitation`(manager: TransactionManager) {
        val userServices = UserServices(manager)
        val inviter = checkNotNull(ChannelServicesTest.makeUser(manager))
        val inviterId = checkNotNull(inviter.uId)
        val result = userServices.createInvitation(inviterId)
        assertIs<Success<UserInvitation>>(result)
        assertEquals(inviter.uId, result.value.inviterId)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to create an invitation with an invalid inviter id should return InviterNotFound`(
        manager: TransactionManager,
    ) {
        val userServices = UserServices(manager)
        val result = userServices.createInvitation(0u)
        assertIs<Failure<UserError.InviterNotFound>>(result)
        assertEquals(UserError.InviterNotFound, result.value)
    }
}