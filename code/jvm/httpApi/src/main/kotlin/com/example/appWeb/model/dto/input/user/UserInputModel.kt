package com.example.appWeb.model.dto.input.user

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import model.Password

/**
 * Represents the input model for a user
 * @param username The username
 * @param password The password
 */
data class UserInputModel(
	@get:NotBlank val username: String,
	@get:NotBlank @get:Pattern(regexp = Password.PASSWORD_PATTERN)
	val password: String,
)
