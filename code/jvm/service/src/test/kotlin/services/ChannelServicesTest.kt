package services

import TransactionManager
import errors.ChannelError
import jdbc.transactionManager.TransactionManagerJDBC
import mem.TransactionManagerInMem
import model.channels.AccessControl.READ_WRITE
import model.channels.Channel
import model.channels.Visibility.PRIVATE
import model.channels.Visibility.PUBLIC
import model.users.Password
import model.users.User
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import utils.Failure
import utils.Success
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
                TransactionManagerJDBC(Environment).also { cleanup(it) },
            )

        private fun cleanup(manager: TransactionManager) =
            manager.run {
                channelRepo.clear()
                userRepo.clear()
                messageRepo.clear()
            }

        private fun makeUser(manager: TransactionManager) =
            manager
                .run {
                    userRepo
                        .createUser(
                            User(
                                username = "owner",
                                password = Password("Password123"),
                            ),
                        )
                }
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `create a new channel`(manager: TransactionManager) {
        val owner = makeUser(manager)
        val channelServices = ChannelServices(manager)
        val ownerId = checkNotNull(owner?.uId) { "Owner id is null" }
        val newChannel = channelServices.createChannel(ownerId, "name", READ_WRITE.name, PUBLIC.name)
        assertIs<Success<Channel>>(newChannel, "Channel creation failed")
        assertNotNull(newChannel.value.channelId, "Channel id is null")
        assertEquals(ownerId, newChannel.value.owner.uId, "Owner id is different")
        assertEquals(
            "@${owner?.username}/name",
            newChannel.value.name.fullName,
            "Channel name is different",
        )
        assertEquals(READ_WRITE, newChannel.value.accessControl, "Access control is different")
        assertIs<Channel.Public>(newChannel.value, "Visibility is different")
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `fail to create a channel due to blank parameters`(manager: TransactionManager) {
        val owner = makeUser(manager)
        val channelServices = ChannelServices(manager)
        val ownerId = checkNotNull(owner?.uId) { "Owner id is null" }
        val paramsList =
            listOf(
                Triple("", READ_WRITE.name, PUBLIC.name),
                Triple("name", "", PUBLIC.name),
                Triple("name", READ_WRITE.name, ""),
            )
        paramsList.forEach { (name, accessControl, visibility) ->
            val newChannel = channelServices.createChannel(ownerId, name, accessControl, visibility)
            assertIs<Failure<ChannelError>>(newChannel, "Channel creation should have failed")
            assertEquals(ChannelError.InvalidChannelInfo, newChannel.value, "Channel error is different")
        }
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `fail to create a channel due to invalid access control`(manager: TransactionManager) {
        val owner = makeUser(manager)
        val channelServices = ChannelServices(manager)
        val ownerId = checkNotNull(owner?.uId) { "Owner id is null" }
        val newChannel = channelServices.createChannel(ownerId, "name", "INVALID)", PUBLIC.name)
        assertIs<Failure<ChannelError>>(newChannel, "Channel creation should have failed")
        assertEquals(ChannelError.InvalidChannelAccessControl, newChannel.value, "Channel error is different")
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `fail to create a channel due to invalid visibility`(manager: TransactionManager) {
        val owner = makeUser(manager)
        val channelServices = ChannelServices(manager)
        val ownerId = checkNotNull(owner?.uId) { "Owner id is null" }
        val newChannel = channelServices.createChannel(ownerId, "name", READ_WRITE.name, "INVALID")
        assertIs<Failure<ChannelError>>(newChannel, "Channel creation should have failed")
        assertEquals(ChannelError.InvalidChannelVisibility, newChannel.value, "Channel error is different")
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `delete a channel`(manager: TransactionManager) {
        val owner = makeUser(manager)
        val channelServices = ChannelServices(manager)
        val ownerId = checkNotNull(owner?.uId) { "Owner id is null" }
        val newChannel = channelServices.createChannel(ownerId, "name", READ_WRITE.name, PUBLIC.name)
        assertIs<Success<Channel>>(newChannel, "Channel creation failed")
        assertNotNull(newChannel.value.channelId, "Channel id is null")
        val newChannelId = checkNotNull(newChannel.value.channelId) { "Channel id is null" }
        val result = channelServices.deleteChannel(newChannelId)
        assertIs<Success<Unit>>(result, "Channel deletion failed")
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `fail to delete a channel due to invalid id`(manager: TransactionManager) {
        val channelServices = ChannelServices(manager)
        val result = channelServices.deleteChannel(0u)
        assertIs<Failure<ChannelError>>(result, "Channel deletion should have failed")
        assertEquals(ChannelError.ChannelNotFound, result.value, "Channel error is different")
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `get channel`(manager: TransactionManager) {
        val owner = makeUser(manager)
        val channelServices = ChannelServices(manager)
        val ownerId = checkNotNull(owner?.uId) { "Owner id is null" }
        val newChannel = channelServices.createChannel(ownerId, "name", READ_WRITE.name, PUBLIC.name)
        assertIs<Success<Channel>>(newChannel, "Channel creation failed")
        assertNotNull(newChannel.value.channelId, "Channel id is null")
        val newChannelId = checkNotNull(newChannel.value.channelId) { "Channel id is null" }
        val result = channelServices.getChannel(newChannelId)
        assertIs<Success<Channel>>(result, "Channel retrieval failed")
        assertEquals(newChannel.value, result.value, "Channel is different")
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `fail to get a channel due to invalid id`(manager: TransactionManager) {
        val channelServices = ChannelServices(manager)
        val result = channelServices.getChannel(0u)
        assertIs<Failure<ChannelError>>(result, "Channel retrieval should have failed")
        assertEquals(ChannelError.ChannelNotFound, result.value, "Channel error is different")
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `get channels`(manager: TransactionManager) {
        val owner = makeUser(manager)
        val channelServices = ChannelServices(manager)
        val ownerId = checkNotNull(owner?.uId) { "Owner id is null" }
        val nr = 5
        repeat(nr) {
            val newChannel = channelServices.createChannel(ownerId, "name$it", READ_WRITE.name, PUBLIC.name)
            assertIs<Success<Channel>>(newChannel, "Channel creation failed")
            assertNotNull(newChannel.value.channelId, "Channel id is null")
        }
        val result = channelServices.getChannels(0u, nr.toUInt())
        assertIs<Success<List<Channel>>>(result, "Channels retrieval failed")
        assertEquals(nr, result.value.size, "Number of channels is different")
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `get channels from userid`(manager: TransactionManager) {
        val owner = makeUser(manager)
        val channelServices = ChannelServices(manager)
        val ownerId = checkNotNull(owner?.uId) { "Owner id is null" }
        val nr = 5
        repeat(nr) {
            val newChannel = channelServices.createChannel(ownerId, "name$it", READ_WRITE.name, PRIVATE.name)
            assertIs<Success<Channel>>(newChannel, "Channel creation failed")
            assertNotNull(newChannel.value.channelId, "Channel id is null")
        }
        val result = channelServices.getChannels(ownerId, 0u, nr.toUInt())
        assertIs<Success<List<Channel>>>(result, "Channels retrieval failed")
        assertEquals(nr, result.value.size, "Number of channels is different")
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `fail to get channels due to invalid owner id`(manager: TransactionManager) {
        val owner = makeUser(manager)
        val channelServices = ChannelServices(manager)
        val ownerId = checkNotNull(owner?.uId) { "Owner id is null" }
        val nr = 5
        repeat(nr) {
            val newChannel = channelServices.createChannel(ownerId, "name$it", READ_WRITE.name, PRIVATE.name)
            assertIs<Success<Channel>>(newChannel, "Channel creation failed")
            assertNotNull(newChannel.value.channelId, "Channel id is null")
        }
        val result = channelServices.getChannels(0u, 0u, nr.toUInt())
        assertIs<Failure<ChannelError>>(result, "Channels retrieval should have failed")
        assertEquals(ChannelError.UserNotFound, result.value, "Channel error is different")
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `create a channel invitation`(manager: TransactionManager) {
        val owner = makeUser(manager)
        val channelServices = ChannelServices(manager)
        val ownerId = checkNotNull(owner?.uId) { "Owner id is null" }
        val newChannel = channelServices.createChannel(ownerId, "name", READ_WRITE.name, PUBLIC.name)
        assertIs<Success<Channel>>(newChannel, "Channel creation failed")
        assertNotNull(newChannel.value.channelId, "Channel id is null")
        val newChannelId = checkNotNull(newChannel.value.channelId) { "Channel id is null" }
        val result =
            channelServices.createChannelInvitation(
                newChannelId,
                ownerId,
                null,
                READ_WRITE.name,
                ownerId,
            )
        assertIs<Success<UUID>>(result, "Channel invitation creation failed")
    }
}