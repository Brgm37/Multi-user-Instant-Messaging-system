package com.example.appWeb.controller

import com.example.appWeb.model.dto.input.user.UserSignUpInputModel
import com.example.appWeb.model.dto.output.user.UserAuthenticationOutputModel
import com.example.appWeb.model.dto.output.user.UserInfoOutputModel
import com.example.appWeb.model.problem.ChannelProblem
import com.example.appWeb.model.problem.Problem
import errors.ChannelError.ChannelNotFound
import errors.ChannelError.InvitationCodeMaxUsesReached
import errors.UserError.InvalidUserInfo
import errors.UserError.InvitationCodeHasExpired
import errors.UserError.InvitationCodeIsInvalid
import errors.UserError.InviterNotFound
import errors.UserError.UserAlreadyExists
import errors.UserError.UserNotFound
import interfaces.UserServicesInterface
import jakarta.inject.Named
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import utils.Failure
import utils.Success

/**
 * Represents the controller for the user
 * @param userService The user service
 */
@RestController
class UserController(
    @Named("UserServices") private val userService: UserServicesInterface,
) {
    @PostMapping(USER_BASE_URL)
    fun createUser(
        @Valid @RequestBody user: UserSignUpInputModel,
        res: HttpServletResponse,
    ) {
        val response =
            userService.createUser(
                username = user.username,
                password = user.password,
                invitationCode = user.invitationCode,
                inviterUId = user.inviterUId,
            )
        when (response) {
            is Success -> {
                setCookie(response.value.token.toString(), res)
                // TODO : check if this is the correct way to set cookie
                ResponseEntity.ok(UserAuthenticationOutputModel.fromDomain(response.value))
            }

            is Failure -> {
                when (response.value) {
                    InvalidUserInfo -> Problem.InvalidUserInfo.response(BAD_REQUEST)
                    UserAlreadyExists -> Problem.UserAlreadyExists.response(BAD_REQUEST)
                    InviterNotFound -> Problem.InviterNotFound.response(BAD_REQUEST)
                    InvitationCodeIsInvalid -> Problem.InvitationCodeIsInvalid.response(BAD_REQUEST)
                    InvitationCodeHasExpired -> Problem.InvitationCodeHasExpired.response(BAD_REQUEST)
                    else -> Problem.UnableToCreateUser.response(BAD_REQUEST)
                }
            }
        }
    }

    @GetMapping(USER_ID_URL)
    fun getUser(
        @PathVariable userId: UInt,
    ) {
        when (val response = userService.getUser(userId)) {
            is Success -> {
                ResponseEntity.ok(UserInfoOutputModel.fromDomain(response.value))
            }

            is Failure -> {
                Problem.UserNotFound.response(NOT_FOUND)
            }
        }
    }

    @PutMapping(CHANNEL_ID_USER_ID_URL)
    fun joinChannel(
        @PathVariable channelId: UInt,
        @PathVariable userId: UInt,
        @RequestParam invitationCode: String = "",
    ) {
        when (val response = userService.joinChannel(userId, channelId, invitationCode)) {
            is Success -> {
                ResponseEntity.ok()
            }

            is Failure -> {
                when (response.value) {
                    UserNotFound -> Problem.UserNotFound.response(NOT_FOUND)
                    ChannelNotFound -> ChannelProblem.ChannelNotFound.response(NOT_FOUND)
                    InvitationCodeIsInvalid -> Problem.InvitationCodeIsInvalid.response(BAD_REQUEST)
                    InvitationCodeHasExpired -> Problem.InvitationCodeHasExpired.response(BAD_REQUEST)
                    InvitationCodeMaxUsesReached -> Problem.InvitationCodeMaxUsesReached.response(BAD_REQUEST)
                    else -> Problem.UnableToJoinChannel.response(BAD_REQUEST)
                }
            }
        }
    }

    private fun setCookie(
        token: String,
        response: HttpServletResponse,
    ) {
        val cookie = Cookie("session", token)
        cookie.isHttpOnly = true
        cookie.secure = true
        cookie.maxAge = 60 * 60 * 24
        response.addCookie(cookie)
    }

    companion object {
        /**
         * The base URL for the user endpoints.
         */
        const val USER_BASE_URL = "/users"

        /**
         * The URL for the user with the given id.
         */
        const val USER_ID_URL = "$USER_BASE_URL/{userId}"

        /**
         * The URL for the user with the given id, the channel with the given id and invitation code.
         */
        const val CHANNEL_ID_USER_ID_URL = "${ChannelController.CHANNEL_ID_URL}$USER_ID_URL"
    }
}