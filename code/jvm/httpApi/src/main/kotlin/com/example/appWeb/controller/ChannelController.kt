package com.example.appWeb.controller

import com.example.appWeb.model.dto.input.channel.CreateChannelInputModel
import com.example.appWeb.model.dto.input.channel.CreateChannelInvitationInputModel
import com.example.appWeb.model.dto.input.channel.JoinChannelInputModel
import com.example.appWeb.model.dto.input.channel.UpdateChannelInputModel
import com.example.appWeb.model.dto.input.user.AuthenticatedUserInputModel
import com.example.appWeb.model.dto.output.channel.AccessControlOutPutModel
import com.example.appWeb.model.dto.output.channel.ChannelInvitationOutputModel
import com.example.appWeb.model.dto.output.channel.ChannelListOutputModel
import com.example.appWeb.model.dto.output.channel.ChannelOutputModel
import com.example.appWeb.model.problem.ChannelProblem
import com.example.appWeb.model.problem.UserProblem
import com.example.appWeb.swagger.ChannelSwaggerConfig
import errors.ChannelError
import errors.ChannelError.InvalidChannelAccessControl
import errors.ChannelError.InvalidChannelInfo
import errors.ChannelError.InvalidChannelVisibility
import errors.ChannelError.UserNotFound
import interfaces.ChannelServicesInterface
import io.swagger.v3.oas.annotations.Parameter
import jakarta.validation.Valid
import org.hibernate.validator.constraints.Range
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import utils.Failure
import utils.Success
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

/**
 * The default limit for the channel list.
 */
private const val LIMIT = 10u

/**
 * The default offset for the channel list.
 */
private const val OFFSET = 0u

/**
 * Represents the controller for the channel
 *
 * @property channelService The channel service
 */
@RestController
@RequestMapping(ChannelController.CHANNEL_BASE_URL)
class ChannelController(
    private val channelService: ChannelServicesInterface,
) {
    /**
     * Decodes the name of the channel.
     *
     * @param name The name of the channel
     * @return The decoded name
     */
    private fun decodeName(name: String): String =
        URLDecoder
            .decode(name, StandardCharsets.UTF_8.toString())

    @GetMapping(CHANNEL_ID_URL)
    @ChannelSwaggerConfig.GetChannel
    fun getChannel(
        @PathVariable @Range(min = 1) cId: UInt,
        @Parameter(hidden = true) authenticated: AuthenticatedUserInputModel,
    ): ResponseEntity<*> =
        when (val response = channelService.getChannel(cId)) {
            is Success -> {
                ResponseEntity.ok(ChannelOutputModel.fromDomain(response.value))
            }

            is Failure -> {
                ChannelProblem.ChannelNotFound.response(NOT_FOUND)
            }
        }

    @GetMapping
    @ChannelSwaggerConfig.GetChannels
    fun getChannels(
        @Parameter(hidden = true) authenticated: AuthenticatedUserInputModel,
        @RequestParam offset: UInt = OFFSET,
        @RequestParam limit: UInt = LIMIT,
    ): ResponseEntity<*> =
        when (val response = channelService.getChannels(offset, limit)) {
            is Failure -> {
                ChannelProblem.ChannelNotFound.response(NOT_FOUND)
            }

            is Success -> {
                ResponseEntity.ok(response.value.map(ChannelListOutputModel::fromDomain))
            }
        }

    @GetMapping(CHANNEL_BY_NAME)
    @ChannelSwaggerConfig.GetChannelByName
    fun getChannelByName(
        @PathVariable name: String,
        @Parameter(hidden = true) authenticated: AuthenticatedUserInputModel,
        @RequestParam offset: UInt = OFFSET,
        @RequestParam limit: UInt = LIMIT,
    ): ResponseEntity<*> =
        when (val response = channelService.getByName(decodeName(name), offset, limit)) {
            is Success -> {
                ResponseEntity.ok(response.value.map(ChannelListOutputModel::fromDomain))
            }

            is Failure -> {
                ChannelProblem.ChannelNotFound.response(NOT_FOUND)
            }
        }

    @PostMapping
    @ChannelSwaggerConfig.CreateChannel
    fun createChannel(
        @Valid @RequestBody channel: CreateChannelInputModel,
        @Parameter(hidden = true) authenticated: AuthenticatedUserInputModel,
    ): ResponseEntity<*> {
        val response =
            channelService.createChannel(
                owner = authenticated.uId,
                name = channel.name,
                visibility = channel.visibility,
                accessControl = channel.accessControl,
                description = channel.description,
                icon = channel.icon,
            )
        return when (response) {
            is Success -> {
                ResponseEntity.ok(ChannelOutputModel.fromDomain(response.value))
            }

            is Failure -> {
                when (response.value) {
                    InvalidChannelInfo -> ChannelProblem.InvalidChannelInfo.response(BAD_REQUEST)
                    InvalidChannelAccessControl -> ChannelProblem.InvalidChannelAccessControl.response(BAD_REQUEST)
                    InvalidChannelVisibility -> ChannelProblem.InvalidChannelVisibility.response(BAD_REQUEST)
                    UserNotFound -> UserProblem.UserNotFound.response(NOT_FOUND)
                    else -> ChannelProblem.UnableToCreateChannel.response(BAD_REQUEST)
                }
            }
        }
    }

    @DeleteMapping(CHANNEL_ID_URL)
    fun deleteChannel(
        @PathVariable @Range(min = 1) cId: UInt,
        @Parameter(hidden = true) authenticated: AuthenticatedUserInputModel,
    ): ResponseEntity<*> =
        when (channelService.deleteOrLeaveChannel(authenticated.uId, cId)) {
            is Success -> ResponseEntity.ok(Unit)
            is Failure -> ChannelProblem.ChannelNotFound.response(NOT_FOUND)
        }

    @PutMapping(CHANNEL_ID_URL)
    fun updateChannel(
        @PathVariable @Range(min = 1) cId: UInt,
        @Valid @RequestBody channel: UpdateChannelInputModel,
        @Parameter(hidden = true) authenticated: AuthenticatedUserInputModel,
    ): ResponseEntity<*> {
        channelService
            .updateChannel(
                id = cId,
                name = channel.name,
                accessControl = channel.accessControl,
                visibility = channel.visibility,
                description = channel.description,
                icon = channel.icon,
            ).let { response ->
                return when (response) {
                    is Success -> {
                        ResponseEntity.ok(ChannelOutputModel.fromDomain(response.value))
                    }

                    is Failure -> {
                        ChannelProblem.ChannelNotFound.response(NOT_FOUND)
                    }
                }
            }
    }

    @PostMapping(CHANNEL_INVITATION_URL)
    @ChannelSwaggerConfig.CreateChannelInvitation
    fun createChannelInvitation(
        @Valid @RequestBody invitation: CreateChannelInvitationInputModel,
        @Parameter(hidden = true) authenticated: AuthenticatedUserInputModel,
    ): ResponseEntity<*> {
        val response =
            channelService.createChannelInvitation(
                channelId = invitation.channelId,
                owner = authenticated.uId,
                maxUses = invitation.maxUses,
                expirationDate = invitation.expirationDate,
                accessControl = invitation.accessControl,
            )
        return when (response) {
            is Success -> {
                ResponseEntity.ok(ChannelInvitationOutputModel(response.value.invitationCode.toString()))
            }

            is Failure -> {
                when (response.value) {
                    ChannelError.ChannelNotFound -> ChannelProblem.ChannelNotFound.response(NOT_FOUND)
                    else -> ChannelProblem.InvalidChannelInfo.response(BAD_REQUEST)
                }
            }
        }
    }

    @PutMapping(CHANNEL_INVITATION_URL)
    fun joinChannel(
        @RequestBody invitation: JoinChannelInputModel,
        @Parameter(hidden = true) authenticated: AuthenticatedUserInputModel,
    ): ResponseEntity<*> {
        val args = arrayOf(invitation.cId, invitation.invitationCode)
        if (args.all { it == null }) return ChannelProblem.UnableToJoinChannel.response(BAD_REQUEST)
        return when (
            val response =
                channelService.joinChannel(authenticated.uId, invitation.cId, invitation.invitationCode)
        ) {
            is Success -> ResponseEntity.ok(ChannelOutputModel.fromDomain(response.value))
            is Failure -> ChannelProblem.UnableToJoinChannel.response(BAD_REQUEST)
        }
    }

    @GetMapping(PUBLIC)
    fun getPublicChannels(
        @RequestParam offset: UInt = OFFSET,
        @RequestParam limit: UInt = LIMIT,
        @Parameter(hidden = true) authenticated: AuthenticatedUserInputModel,
    ): ResponseEntity<*> =
        when (val response = channelService.getPublic(authenticated.uId, offset, limit)) {
            is Success -> {
                ResponseEntity.ok(response.value.map(ChannelListOutputModel::fromDomain))
            }

            is Failure -> {
                ChannelProblem.ChannelNotFound.response(NOT_FOUND)
            }
        }

    @GetMapping(PUBLIC_BY_NAME)
    fun getPublicChannelsByName(
        @PathVariable name: String,
        @RequestParam offset: UInt = OFFSET,
        @RequestParam limit: UInt = LIMIT,
        @Parameter(hidden = true) authenticated: AuthenticatedUserInputModel,
    ): ResponseEntity<*> =
        when (val response = channelService.getPublicByName(authenticated.uId, decodeName(name), offset, limit)) {
            is Success -> {
                ResponseEntity.ok(response.value.map(ChannelListOutputModel::fromDomain))
            }

            is Failure -> {
                ChannelProblem.ChannelNotFound.response(NOT_FOUND)
            }
        }

    @GetMapping(MY_CHANNELS)
    fun getMyChannels(
        @Parameter(hidden = true) authenticated: AuthenticatedUserInputModel,
        @RequestParam offset: UInt = OFFSET,
        @RequestParam limit: UInt = LIMIT,
    ): ResponseEntity<*> =
        when (val response = channelService.getChannels(authenticated.uId, offset, limit)) {
            is Success -> {
                ResponseEntity.ok(response.value.map(ChannelListOutputModel::fromDomain))
            }

            is Failure -> {
                ChannelProblem.ChannelNotFound.response(NOT_FOUND)
            }
        }

    @GetMapping(MY_CHANNELS_BY_NAME)
    fun getMyChannelsWithName(
        @PathVariable name: String,
        @Parameter(hidden = true) authenticated: AuthenticatedUserInputModel,
        @RequestParam offset: UInt = OFFSET,
        @RequestParam limit: UInt = LIMIT,
    ): ResponseEntity<*> =
        when (val response = channelService.getByName(authenticated.uId, decodeName(name), offset, limit)) {
            is Success -> {
                ResponseEntity.ok(response.value.map(ChannelListOutputModel::fromDomain))
            }

            is Failure -> {
                ChannelProblem.ChannelNotFound.response(NOT_FOUND)
            }
        }

    @GetMapping(ACCESS_CONTROL)
    fun getAccessControl(
        @PathVariable cId: UInt,
        @Parameter(hidden = true) authenticated: AuthenticatedUserInputModel,
    ): ResponseEntity<*> =
        when (val response = channelService.getAccessControl(authenticated.uId, cId)) {
            is Success -> ResponseEntity.ok(AccessControlOutPutModel.fromAccessControl(response.value))
            is Failure -> ChannelProblem.AccessControlNotFound.response(NOT_FOUND)
        }

    companion object {
        /**
         * The base URL for the channel endpoints.
         */
        const val CHANNEL_BASE_URL = "/api/channels"

        /**
         * The URL for the channel with the given id.
         */
        const val CHANNEL_ID_URL = "/{cId}"

        /**
         * The URL for the channel with the given name.
         */
        const val CHANNEL_BY_NAME = "/name/{name}"

        /**
         * The URL for the channel invitations.
         */
        const val CHANNEL_INVITATION_URL = "/invitations"

        /**
         * The URL for the user's channels.
         */
        const val MY_CHANNELS = "/my"

        /**
         * The URL for the user's channels with the given name.
         */
        const val MY_CHANNELS_BY_NAME = "${MY_CHANNELS}/{name}"

        /**
         * The URL for the public channels.
         */
        const val PUBLIC = "/public"

        /**
         * The URL for the public channels with the given name.
         */
        const val PUBLIC_BY_NAME = "$PUBLIC/{name}"

        /**
         * The URL for the access control of a user in a channel.
         */
        const val ACCESS_CONTROL = "/accessControl/{cId}"
    }
}