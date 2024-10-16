package com.example.appWeb

import TransactionManager
import com.example.appWeb.controller.ChannelController
import com.example.appWeb.model.dto.input.channel.CreateChannelInputModel
import com.example.appWeb.model.dto.input.channel.CreateChannelInvitationInputModel
import com.example.appWeb.model.dto.input.user.AuthenticatedUserInputModel
import com.example.appWeb.model.dto.output.channel.ChannelListOutputModel
import com.example.appWeb.model.dto.output.channel.ChannelOutputModel
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import jdbc.transactionManager.TransactionManagerJDBC
import mem.TransactionManagerInMem
import model.channels.AccessControl.READ_WRITE
import model.channels.Channel
import model.channels.Visibility.PUBLIC
import model.users.Password
import model.users.User
import model.users.UserToken
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.HttpStatus
import services.ChannelServices
import utils.Success
import java.util.UUID
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ChannelControllerTest {
    companion object {
        private val hikari =
            HikariConfig()
                .apply {
                    jdbcUrl = Environment.connectionUrl
                    username = Environment.username
                    password = Environment.password
                    maximumPoolSize = Environment.poolSize
                }.let { HikariDataSource(it) }

        @JvmStatic
        fun transactionManager(): Stream<TransactionManager> =
            Stream.of(
                TransactionManagerInMem().also { cleanup(it) },
                TransactionManagerJDBC(hikari).also { cleanup(it) },
            )

        private fun cleanup(manager: TransactionManager) {
            manager.run {
                channelRepo.clear()
                messageRepo.clear()
                userRepo.clear()
            }
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

        private fun makeToken(
            manager: TransactionManager,
            uId: UInt,
        ) = manager
            .run {
                val token = UserToken(userId = uId, token = UUID.randomUUID())
                userRepo
                    .createToken(token)
                token
            }
    }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `get a channel`(manager: TransactionManager) {
        val owner = makeUser(manager)
        val ownerId = checkNotNull(owner?.uId) { "Owner id is null" }
        val channelServices = ChannelServices(manager)
        val channelController = ChannelController(channelServices)
        val newChannel = channelServices.createChannel(ownerId, "name", READ_WRITE.name, PUBLIC.name)
        assertIs<Success<Channel>>(newChannel, "Channel creation failed")
        val cId = checkNotNull(newChannel.value.channelId) { "Channel id is null" }
        val u = manager.run { userRepo.findById(ownerId) }
        checkNotNull(u) { "User not found" }
        val authenticated = AuthenticatedUserInputModel(ownerId, makeToken(manager, ownerId).token.toString())
        channelController
            .getChannel(
                cId,
                authenticated,
            ).let { resp ->
                assertEquals(HttpStatus.OK, resp.statusCode, "Status code is different")
                assertIs<ChannelOutputModel>(resp.body, "Body is not a ChannelOutputModel")
                val outputModel = resp.body as ChannelOutputModel
                assertEquals(cId, outputModel.id, "Channel id is different")
                assertEquals(ownerId, outputModel.owner.id, "Owner id is different")
                assertEquals("@owner/name", outputModel.name.name, "Channel name is different")
                assertEquals(READ_WRITE.name, outputModel.accessControl, "Access control is different")
                assertEquals(PUBLIC.name, outputModel.visibility, "Visibility is different")
            }
    }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `fail to get a channel`(manager: TransactionManager) {
        val owner = makeUser(manager)
        val uId = checkNotNull(owner?.uId) { "Owner id is null" }
        val u = manager.run { userRepo.findById(uId) }
        checkNotNull(u) { "User not found" }
        val authenticated = AuthenticatedUserInputModel(uId, makeToken(manager, uId).token.toString())
        val channelServices = ChannelServices(manager)
        val channelController = ChannelController(channelServices)
        val cId = 1u
        channelController
            .getChannel(cId, authenticated)
            .let { resp ->
                assertEquals(HttpStatus.NOT_FOUND, resp.statusCode, "Status code is different")
            }
    }

    @ParameterizedTest
    @MethodSource("transactionManager")
    @Suppress("UNCHECKED_CAST")
    fun `get channels`(manager: TransactionManager) {
        val owner = makeUser(manager)
        val ownerId = checkNotNull(owner?.uId) { "Owner id is null" }
        val channelServices = ChannelServices(manager)
        val channelController = ChannelController(channelServices)
        val nr = 10
        repeat(nr) {
            channelServices.createChannel(ownerId, "name$it", READ_WRITE.name, PUBLIC.name)
        }
        val u = manager.run { userRepo.findById(ownerId) }
        checkNotNull(u) { "User not found" }
        val authenticated = AuthenticatedUserInputModel(ownerId, makeToken(manager, ownerId).token.toString())
        channelController
            .getChannels(authenticated)
            .let { resp ->
                assertEquals(HttpStatus.OK, resp.statusCode, "Status code is different")
                assertIs<List<ChannelListOutputModel>>(resp.body, "Body is not a List<ChannelOutputModel>")
                val outputModels = resp.body as List<ChannelListOutputModel>
                assertEquals(nr, outputModels.size, "Number of channels is different")
                val outputModel = outputModels.first()
                assertEquals(ownerId, outputModel.ownerOutputModel.id, "Owner id is different")
            }
    }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `create channel`(manager: TransactionManager) {
        val owner = makeUser(manager)
        val ownerId = checkNotNull(owner?.uId) { "Owner id is null" }
        val channelServices = ChannelServices(manager)
        val channelController = ChannelController(channelServices)
        val channelName = "name"
        val accessControl = READ_WRITE.name
        val visibility = PUBLIC.name
        val u = manager.run { userRepo.findById(ownerId) }
        checkNotNull(u) { "User not found" }
        val authenticated = AuthenticatedUserInputModel(ownerId, makeToken(manager, ownerId).token.toString())
        channelController
            .createChannel(
                CreateChannelInputModel(
                    name = channelName,
                    accessControl = accessControl,
                    visibility = visibility,
                ),
                authenticated,
            ).let { resp ->
                assertEquals(HttpStatus.OK, resp.statusCode, "Status code is different")
                assertIs<ChannelOutputModel>(resp.body, "Body is not a ChannelOutputModel")
                val outputModel = resp.body as ChannelOutputModel
                assertEquals(ownerId, outputModel.owner.id, "Owner id is different")
                assertEquals("@${owner?.username}/$channelName", outputModel.name.name, "Channel name is different")
                assertEquals(accessControl, outputModel.accessControl, "Access control is different")
                assertEquals(visibility, outputModel.visibility, "Visibility is different")
            }
    }

    @ParameterizedTest
    @MethodSource("transactionManager")
    fun `create channel invitation`(manager: TransactionManager) {
        val owner = makeUser(manager)
        val ownerId = checkNotNull(owner?.uId) { "Owner id is null" }
        val channelServices = ChannelServices(manager)
        val channelController = ChannelController(channelServices)
        val channelName = "name"
        val accessControl = READ_WRITE.name
        val visibility = PUBLIC.name
        val newChannel = channelServices.createChannel(ownerId, channelName, accessControl, visibility)
        assertIs<Success<Channel>>(newChannel, "Channel creation failed")
        val cId = checkNotNull(newChannel.value.channelId) { "Channel id is null" }
        val authenticated = AuthenticatedUserInputModel(ownerId, makeToken(manager, ownerId).token.toString())
        val invitation =
            channelController.createChannelInvitation(
                CreateChannelInvitationInputModel(
                    channelId = cId,
                    maxUses = 1u,
                    expirationDate = null,
                    accessControl = accessControl,
                ),
                authenticated,
            )
        assertIs<UUID>(invitation.body, "Body is not a ChannelOutputModel")
    }
}