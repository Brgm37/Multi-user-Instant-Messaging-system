package com.example.appWeb.model.dto.input.user

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import model.users.Password

/**
 * Represents the user input model for the sign-up operation.
 * @param username The username of the user
 * @param password The password of the user
 * @param invitationCode The invitation code to join the app
 * @param inviterUId The inviter user id
 */
data class UserSignUpInputModel(
	@get:NotBlank val username: String,
	@get:NotBlank @get:Pattern(regexp = Password.PASSWORD_PATTERN)
	val password: String,
	@get:NotBlank val invitationCode: String,
	val inviterUId: UInt,
)
