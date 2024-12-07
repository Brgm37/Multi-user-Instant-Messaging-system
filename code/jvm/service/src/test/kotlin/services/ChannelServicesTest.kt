package services

import TransactionManager
import errors.ChannelError
import jdbc.transactionManager.TransactionManagerJDBC
import mem.TransactionManagerInMem
import model.channels.AccessControl.READ_WRITE
import model.channels.Channel
import model.channels.ChannelInvitation
import model.channels.ChannelName
import model.channels.Visibility.PRIVATE
import model.channels.Visibility.PUBLIC
import model.users.Password
import model.users.User
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import utils.Failure
import utils.Success
import utils.encryption.DummyEncrypt
import java.time.LocalDate
import java.util.UUID
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class ChannelServicesTest {
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

        fun makeUser(
            manager: TransactionManager,
            username: String = "owner",
        ) = manager
            .run {
                userRepo
                    .createUser(
                        User(
                            username = username,
                            password = Password("Password123"),
                        ),
                    )
            }

        fun testSetup(
            manager: TransactionManager,
            runnable: ChannelServices.(User) -> Unit,
        ) {
            val owner = makeUser(manager)
            assertNotNull(owner, "Owner is null")
            ChannelServices(manager).runnable(owner)
        }
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `create a new channel`(manager: TransactionManager) =
        testSetup(manager) { user ->
            val uId = checkNotNull(user.uId) { "Owner id is null" }
            val name = "name"
            val newChannel = createChannel(uId, name, READ_WRITE.name, PUBLIC.name)
            assertIs<Success<Channel>>(newChannel, "Channel creation failed")
            assertNotNull(newChannel.value.cId, "Channel id is null")
            assertEquals(uId, newChannel.value.owner.uId, "Owner id is different")
            val expectedName = ChannelName(name, user.username)
            assertEquals(
                expectedName.fullName,
                newChannel.value.name.fullName,
                "Channel name is different",
            )
            assertEquals(READ_WRITE, newChannel.value.accessControl, "Access control is different")
            assertIs<Channel.Public>(newChannel.value, "Visibility is different")
        }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `fail to create a channel due to blank parameters`(manager: TransactionManager) =
        testSetup(manager) { user ->
            val uId = checkNotNull(user.uId) { "Owner id is null" }
            val paramsList =
                listOf(
                    Triple("", READ_WRITE.name, PUBLIC.name),
                    Triple("name", "", PUBLIC.name),
                    Triple("name", READ_WRITE.name, ""),
                )
            paramsList.forEach { (name, accessControl, visibility) ->
                val newChannel = createChannel(uId, name, accessControl, visibility)
                assertIs<Failure<ChannelError>>(newChannel, "Channel creation should have failed")
                assertEquals(ChannelError.InvalidChannelInfo, newChannel.value, "Channel error is different")
            }
        }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `fail to create a channel due to invalid access control`(manager: TransactionManager) =
        testSetup(manager) { user ->
            val uId = checkNotNull(user.uId) { "Owner id is null" }
            val newChannel = createChannel(uId, "name", "INVALID", PUBLIC.name)
            assertIs<Failure<ChannelError>>(newChannel, "Channel creation should have failed")
            assertEquals(ChannelError.InvalidChannelAccessControl, newChannel.value, "Channel error is different")
        }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `fail to create a channel due to invalid visibility`(manager: TransactionManager) =
        testSetup(manager) { user ->
            val uId = checkNotNull(user.uId) { "Owner id is null" }
            val newChannel = createChannel(uId, "name", READ_WRITE.name, "INVALID")
            assertIs<Failure<ChannelError>>(newChannel, "Channel creation should have failed")
            assertEquals(ChannelError.InvalidChannelVisibility, newChannel.value, "Channel error is different")
        }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `delete a channel`(manager: TransactionManager) =
        testSetup(manager) { user ->
            val uId = checkNotNull(user.uId) { "Owner id is null" }
            val newChannel = createChannel(uId, "name", READ_WRITE.name, PUBLIC.name)
            assertIs<Success<Channel>>(newChannel, "Channel creation failed")
            assertNotNull(newChannel.value.cId, "Channel id is null")
            val newChannelId = checkNotNull(newChannel.value.cId) { "Channel id is null" }
            val result = deleteChannel(newChannelId)
            assertIs<Success<Unit>>(result, "Channel deletion failed")
        }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `fail to delete a channel due to invalid id`(manager: TransactionManager) =
        testSetup(manager) { _ ->
            val result = deleteChannel(0u)
            assertIs<Failure<ChannelError>>(result, "Channel deletion should have failed")
            assertEquals(ChannelError.ChannelNotFound, result.value, "Channel error is different")
        }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `get channel`(manager: TransactionManager) =
        testSetup(manager) { user ->
            val uId = checkNotNull(user.uId) { "Owner id is null" }
            val newChannel = createChannel(uId, "name", READ_WRITE.name, PUBLIC.name)
            assertIs<Success<Channel>>(newChannel, "Channel creation failed")
            assertNotNull(newChannel.value.cId, "Channel id is null")
            val newChannelId = checkNotNull(newChannel.value.cId) { "Channel id is null" }
            val result = getChannel(newChannelId)
            assertIs<Success<Channel>>(result, "Channel retrieval failed")
            assertEquals(newChannel.value, result.value, "Channel is different")
        }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `fail to get a channel due to invalid id`(manager: TransactionManager) =
        testSetup(manager) { _ ->
            val result = getChannel(0u)
            assertIs<Failure<ChannelError>>(result, "Channel retrieval should have failed")
            assertEquals(ChannelError.ChannelNotFound, result.value, "Channel error is different")
        }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `get channels`(manager: TransactionManager) =
        testSetup(manager) { user ->
            val ownerId = checkNotNull(user.uId) { "Owner id is null" }
            val nr = 5
            repeat(nr) {
                val newChannel = createChannel(ownerId, "name$it", READ_WRITE.name, PUBLIC.name)
                assertIs<Success<Channel>>(newChannel, "Channel creation failed")
                assertNotNull(newChannel.value.cId, "Channel id is null")
            }
            val result = getChannels(0u, nr.toUInt())
            assertIs<Success<List<Channel>>>(result, "Channels retrieval failed")
            assertEquals(nr, result.value.size, "Number of channels is different")
        }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `get channels from userid`(manager: TransactionManager) =
        testSetup(manager) { user ->
            val ownerId = checkNotNull(user.uId) { "Owner id is null" }
            val nr = 5
            repeat(nr) {
                val newChannel = createChannel(ownerId, "name$it", READ_WRITE.name, PUBLIC.name)
                assertIs<Success<Channel>>(newChannel, "Channel creation failed")
                assertNotNull(newChannel.value.cId, "Channel id is null")
            }
            val result = getChannels(ownerId, 0u, nr.toUInt())
            assertIs<Success<List<Channel>>>(result, "Channels retrieval failed")
            assertEquals(nr, result.value.size, "Number of channels is different")
        }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `fail to get channels due to invalid owner id`(manager: TransactionManager) =
        testSetup(manager) { user ->
            val uId = checkNotNull(user.uId) { "Owner id is null" }
            val nr = 5
            repeat(nr) {
                val newChannel = createChannel(uId, "name$it", READ_WRITE.name, PRIVATE.name)
                assertIs<Success<Channel>>(newChannel, "Channel creation failed")
                assertNotNull(newChannel.value.cId, "Channel id is null")
            }
            val result = getChannels(0u, 0u, nr.toUInt())
            assertIs<Failure<ChannelError>>(result, "Channels retrieval should have failed")
            assertEquals(ChannelError.UserNotFound, result.value, "Channel error is different")
        }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `create a channel invitation`(manager: TransactionManager) =
        testSetup(manager) { user ->
            val uId = checkNotNull(user.uId) { "Owner id is null" }
            val newChannel = createChannel(uId, "name", READ_WRITE.name, PUBLIC.name)
            assertIs<Success<Channel>>(newChannel, "Channel creation failed")
            assertNotNull(newChannel.value.cId, "Channel id is null")
            val newChannelId = checkNotNull(newChannel.value.cId) { "Channel id is null" }
            val result =
                createChannelInvitation(
                    newChannelId,
                    uId,
                    null,
                    READ_WRITE.name,
                    uId,
                )
            assertIs<Success<UUID>>(result, "Channel invitation creation failed")
        }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `create a channel invitation with default access control`(manager: TransactionManager) =
        testSetup(manager) { user ->
            val uId = checkNotNull(user.uId) { "Owner id is null" }
            val newChannel = createChannel(uId, "name", READ_WRITE.name, PUBLIC.name)
            assertIs<Success<Channel>>(newChannel, "Channel creation failed")
            assertNotNull(newChannel.value.cId, "Channel id is null")
            val newChannelId = checkNotNull(newChannel.value.cId) { "Channel id is null" }
            val result =
                createChannelInvitation(
                    newChannelId,
                    uId,
                    LocalDate.now().plusDays(1).toString(),
                    null,
                    uId,
                )
            assertIs<Success<UUID>>(result, "Channel invitation creation failed")
        }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `find a channel by its name`(manager: TransactionManager) =
        testSetup(manager) { user ->
            val uId = checkNotNull(user.uId) { "Owner id is null" }
            val name = "name"
            val newChannel = createChannel(uId, name, READ_WRITE.name, PUBLIC.name)
            assertIs<Success<Channel>>(newChannel, "Channel creation failed")
            assertNotNull(newChannel.value.cId, "Channel id is null")
            val result = getPublicByName(newChannel.value.name.fullName)
            assertIs<Success<Channel>>(result, "Channel retrieval failed")
            assertEquals(newChannel.value, result.value, "Channel is different")
        }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `fail to find a channel by its name`(manager: TransactionManager) =
        testSetup(manager) { _ ->
            val result = getPublicByName("name")
            assertIs<Failure<ChannelError>>(result, "Channel retrieval should have failed")
            assertEquals(ChannelError.ChannelNotFound, result.value, "Channel error is different")
        }

//    @ParameterizedTest
//    @MethodSource("transactionManagers")
//    fun `get all channels with name`(manager: TransactionManager) =
//        testSetup(manager) { user ->
//            val ownerId = checkNotNull(user.uId) { "Owner id is null" }
//            val nr = 5
//            repeat(nr) {
//                val newChannel = createChannel(ownerId, "name$it", READ_WRITE.name, PUBLIC.name)
//                assertIs<Success<Channel>>(newChannel, "Channel creation failed")
//                assertNotNull(newChannel.value.cId, "Channel id is null")
//            }
//            val result = getPublicByName("name", 0u, nr.toUInt())
//            assertIs<Success<List<Channel>>>(result, "Channels retrieval failed")
//            assertEquals(nr, result.value.size, "Number of channels is different")
//        }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `update a channel`(manager: TransactionManager) =
        testSetup(manager) { user ->
            val uId = checkNotNull(user.uId) { "Owner id is null" }
            val newChannel = createChannel(uId, "name", READ_WRITE.name, PUBLIC.name)
            assertIs<Success<Channel>>(newChannel, "Channel creation failed")
            assertNotNull(newChannel.value.cId, "Channel id is null")
            val newChannelId = checkNotNull(newChannel.value.cId) { "Channel id is null" }
            val result =
                updateChannel(
                    newChannelId,
                    "newName",
                    READ_WRITE.name,
                    PUBLIC.name,
                    "newDescription",
                    "newIcon",
                )
            assertIs<Success<Channel>>(result, "Channel update failed")
            assertEquals("newName", result.value.name.name, "Channel name is different")
            assertEquals(READ_WRITE, result.value.accessControl, "Access control is different")
            assertIs<Channel.Public>(result.value, "Visibility is different")
            assertEquals("newDescription", result.value.description, "Description is different")
            assertEquals("newIcon", result.value.icon, "Icon is different")
        }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `join a channel with invitation code that has reached the max uses should return InvitationCodeMaxUsesReached`(
        manager: TransactionManager,
    ) = testSetup(manager) { user ->
        val uId = checkNotNull(user.uId) { "Owner id is null" }
        val newChannel = createChannel(uId, "name", READ_WRITE.name, PRIVATE.name)
        assertIs<Success<Channel>>(newChannel, "Channel creation failed")
        val cId = assertNotNull(newChannel.value.cId, "Channel id is null")
        val invitationCode = createChannelInvitation(cId, 0u, null, READ_WRITE.name, uId)
        assertIs<Success<ChannelInvitation>>(invitationCode, "Channel invitation creation failed")
        val user2 = makeUser(manager, "user2")
        val user2Id = checkNotNull(user2?.uId) { "User2 id is null" }
        val result = joinChannel(user2Id, cId, invitationCode.value.invitationCode.toString())
        assertIs<Failure<ChannelError>>(result, "Channel join should have failed")
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `joining a Public channel successfully should return Channel`(manager: TransactionManager) =
        testSetup(manager) { user ->
            val uId = checkNotNull(user.uId) { "Owner id is null" }
            val newChannel = createChannel(uId, "name", READ_WRITE.name, PUBLIC.name)
            assertIs<Success<Channel>>(newChannel, "Channel creation failed")
            val cId = assertNotNull(newChannel.value.cId, "Channel id is null")
            val user2 = makeUser(manager, "user2")
            val user2Id = checkNotNull(user2?.uId) { "User2 id is< null" }
            val result = joinChannel(user2Id, cId, null)
            assertIs<Success<Channel>>(result, "Channel join failed")
            assertEquals(newChannel.value.cId, result.value.cId, "Channel join failed")
        }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `joining a Private channel successfully should return Channel`(manager: TransactionManager) =
        testSetup(manager) { user ->
            val uId = checkNotNull(user.uId) { "Owner id is null" }
            val newChannel = createChannel(uId, "name", READ_WRITE.name, PRIVATE.name)
            assertIs<Success<Channel>>(newChannel, "Channel creation failed")
            val cId = assertNotNull(newChannel.value.cId, "Channel id is null")
            val invitationCode = createChannelInvitation(cId, 1u, null, READ_WRITE.name, uId)
            assertIs<Success<ChannelInvitation>>(invitationCode, "Channel invitation creation failed")
            val user2 = makeUser(manager, "user2")
            val user2Id = checkNotNull(user2?.uId) { "User2 id is null" }
            val result = joinChannel(user2Id, cId, invitationCode.value.invitationCode.toString())
            assertIs<Success<Channel>>(result, "Channel join failed")
            assertEquals(newChannel.value.cId, result.value.cId, "Channel join failed")
        }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to join a channel with a nonexistent channel should return ChannelNotFound`(
        manager: TransactionManager,
    ) = testSetup(manager) { user ->
        val userId = checkNotNull(user.uId) { "User id is null" }
        val result = joinChannel(userId, 0u, null)
        assertIs<Failure<ChannelError>>(result, "Channel join should have failed")
        assertEquals(ChannelError.ChannelNotFound, result.value, "Channel error is different")
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to join a channel with a nonexistent user should return UserNotFound`(manager: TransactionManager) =
        testSetup(manager) { user ->
            val uId = checkNotNull(user.uId) { "Owner id is null" }
            val newChannel = createChannel(uId, "name", READ_WRITE.name, PUBLIC.name)
            assertIs<Success<Channel>>(newChannel, "Channel creation failed")
            val cId = assertNotNull(newChannel.value.cId, "Channel id is null")
            val result = joinChannel(0u, cId, null)
            assertIs<Failure<ChannelError>>(result, "Channel join should have failed")
            assertEquals(ChannelError.UserNotFound, result.value, "Channel error is different")
        }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to join a channel with a user that is already in the channel should return Channel`(
        manager: TransactionManager,
    ) = testSetup(manager) { user ->
        val uId = checkNotNull(user.uId) { "Owner id is null" }
        val newChannel = createChannel(uId, "name", READ_WRITE.name, PUBLIC.name)
        assertIs<Success<Channel>>(newChannel, "Channel creation failed")
        val cId = assertNotNull(newChannel.value.cId, "Channel id is null")
        val result = joinChannel(uId, cId, null)
        assertIs<Success<Channel>>(result, "Channel join failed")
        assertEquals(newChannel.value.cId, result.value.cId, "Channel join failed")
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `trying to join a channel with an expired invitation should return InvitationCodeHasExpired`(
        manager: TransactionManager,
    ) = testSetup(manager) { user ->
        val uId = checkNotNull(user.uId) { "Owner id is null" }
        val newChannel = createChannel(uId, "name", READ_WRITE.name, PRIVATE.name)
        assertIs<Success<Channel>>(newChannel, "Channel creation failed")
        val cId = assertNotNull(newChannel.value.cId, "Channel id is null")
        val invitationCode =
            createChannelInvitation(
                cId,
                0u,
                LocalDate.now().minusDays(1).toString(),
                READ_WRITE.name,
                uId,
            )
        assertIs<Success<ChannelInvitation>>(invitationCode, "Channel invitation creation failed")
        val user2 = makeUser(manager, "user2")
        val user2Id = checkNotNull(user2?.uId) { "User2 id is null" }
        val result = joinChannel(user2Id, cId, invitationCode.value.invitationCode.toString())
        assertIs<Failure<ChannelError>>(result, "Channel join should have failed")
        assertEquals(ChannelError.InvitationCodeHasExpired, result.value, "Channel error is different")
    }
}