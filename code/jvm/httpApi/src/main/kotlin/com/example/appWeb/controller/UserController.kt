package com.example.appWeb.controller

import com.example.appWeb.model.dto.input.user.UserInputModel
import com.example.appWeb.model.dto.output.user.UserAuthenticationOutputModel
import com.example.appWeb.model.dto.output.user.UserInfoOutputModel
import com.example.appWeb.model.problem.Problem
import errors.ChannelError
import errors.UserError.UserNotFound
import errors.UserError.UserAlreadyExists
import errors.UserError.InvalidUserInfo
import interfaces.UserServicesInterface
import jakarta.inject.Inject
import jakarta.inject.Named
import jakarta.validation.Valid
import org.springframework.http.HttpStatus.BAD_REQUEST
import model.User
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import utils.Failure
import utils.Success

/**
 * Represents the controller for the user
 * @param userService The user service
 */
@Controller
class UserController @Inject constructor(
	@Named("UserServices") private val userService: UserServicesInterface
) {

	@PostMapping("/users")
	fun createUser(
		@Valid @RequestBody user: UserInputModel
	) {
		val response = userService.createUser(
			User(
				username = user.username,
				password = user.password
			)
		)
		when (response) {
			is Success -> {
				ResponseEntity.ok(UserAuthenticationOutputModel.fromDomain(response.value))
			}

			is Failure -> {
				when (response.value) {
					InvalidUserInfo -> Problem.InvalidUserInfo.response(BAD_REQUEST)
					UserAlreadyExists -> Problem.UserAlreadyExists.response(BAD_REQUEST)
					else -> Problem.UnableToCreateUser.response(BAD_REQUEST)
				}
			}
		}
	}

	@GetMapping("/users/{id}")
	fun getUser(@PathVariable id: UInt) {
		when (val response = userService.getUser(id)) {
			is Success -> {
				ResponseEntity.ok(UserInfoOutputModel.fromDomain(response.value))
			}
			is Failure -> {
				Problem.UserNotFound.response(NOT_FOUND)
			}
		}

	}

	@PutMapping("/channels/{channelId}/{userId}")
	fun joinChannel(
		@PathVariable channelId: UInt,
		@PathVariable userId: UInt
	) {
		when (val response = userService.joinChannel(userId, channelId)) {
			is Success -> {
				ResponseEntity.ok()
			}
			is Failure -> {
				when (response.value) {
					UserNotFound -> Problem.UserNotFound.response(NOT_FOUND)
					ChannelError.ChannelNotFound -> Problem.ChannelNotFound.response(NOT_FOUND)
					else -> Problem.UnableToJoinChannel.response(BAD_REQUEST)
				}
			}
		}
	}
}