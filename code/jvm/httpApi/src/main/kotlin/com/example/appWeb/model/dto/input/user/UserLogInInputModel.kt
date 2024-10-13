package com.example.appWeb.model.dto.input.user

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import model.users.Password

/**
 * Represents the user input model for the log-in operation.
 * @param username The username of the user to log in
 * @param password The password of the user to log in
 */
data class UserLogInInputModel(
    @get:NotBlank val username: String,
    @get:NotBlank @get:Pattern(regexp = Password.PASSWORD_PATTERN)
    val password: String,
)