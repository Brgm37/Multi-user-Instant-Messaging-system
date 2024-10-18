package com.example.appWeb.controller

import com.example.appWeb.model.dto.input.channel.CreateChannelInputModel
import com.example.appWeb.model.dto.input.channel.CreateChannelInvitationInputModel
import com.example.appWeb.model.dto.input.user.AuthenticatedUserInputModel
import com.example.appWeb.model.dto.output.channel.ChannelListOutputModel
import com.example.appWeb.model.dto.output.channel.ChannelOutputModel
import com.example.appWeb.model.problem.ChannelProblem
import com.example.appWeb.model.problem.UserProblem
import errors.ChannelError
import errors.ChannelError.InvalidChannelAccessControl
import errors.ChannelError.InvalidChannelInfo
import errors.ChannelError.InvalidChannelVisibility
import errors.ChannelError.UserNotFound
import interfaces.ChannelServicesInterface
import jakarta.validation.Valid
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import utils.Failure
import utils.Success

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
    @GetMapping(CHANNEL_ID_URL)
    fun getChannel(
        @PathVariable channelId: UInt,
        authenticated: AuthenticatedUserInputModel,
    ): ResponseEntity<*> =
        when (val response = channelService.getChannel(channelId)) {
            is Success -> {
                ResponseEntity.ok(ChannelOutputModel.fromDomain(response.value))
            }

            is Failure -> {
                ChannelProblem.ChannelNotFound.response(NOT_FOUND)
            }
        }

    @GetMapping
    fun getChannels(
        authenticated: AuthenticatedUserInputModel,
        @RequestParam offset: UInt = OFFSET,
        @RequestParam limit: UInt = LIMIT,
    ): ResponseEntity<*> =
        when (val response = channelService.getChannels(offset, limit)) {
            is Failure -> {
                ChannelProblem.ChannelNotFound.response(NOT_FOUND)
            }

            is Success -> {
                ResponseEntity
                    .ok(
                        response
                            .value
                            .map(ChannelListOutputModel::fromDomain),
                    )
            }
        }

    @PostMapping
    fun createChannel(
        @Valid @RequestBody channel: CreateChannelInputModel,
        authenticated: AuthenticatedUserInputModel,
    ): ResponseEntity<*> {
        val response =
            channelService.createChannel(
                owner = authenticated.uId,
                name = channel.name,
                visibility = channel.visibility,
                accessControl = channel.accessControl,
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

    @PostMapping(CHANNEL_INVITATION_URL)
    fun createChannelInvitation(
        @Valid @RequestBody invitation: CreateChannelInvitationInputModel,
        authenticated: AuthenticatedUserInputModel,
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
                ResponseEntity.ok(response.value)
            }

            is Failure -> {
                when (response.value) {
                    ChannelError.ChannelNotFound -> ChannelProblem.ChannelNotFound.response(NOT_FOUND)
                    else -> ChannelProblem.InvalidChannelInfo.response(BAD_REQUEST)
                }
            }
        }
    }

    companion object {
        /**
         * The base URL for the channel endpoints.
         */
        const val CHANNEL_BASE_URL = "/api/channels"

        /**
         * The URL for the channel with the given id.
         */
        const val CHANNEL_ID_URL = "/{channelId}"

        /**
         * The URL for the channel invitations.
         */
        const val CHANNEL_INVITATION_URL = "/invitations"
    }
}