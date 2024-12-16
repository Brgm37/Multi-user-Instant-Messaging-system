package com.example.appWeb.model.dto.input.user

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import model.users.Password

/**
 * Represents the user input model for the sign-up operation.
 *
 * @property username The username of the user signing up.
 * @property password The password of the user signing up.
 * @property invitationCode The invitation code used to sign up.
 */
data class UserSignUpInputModel(
    @get:NotBlank val username: String,
    @get:NotBlank @get:Pattern(regexp = Password.PASSWORD_PATTERN)
    val password: String,
    @get:NotBlank val invitationCode: String,
)