package com.example.appWeb.model.dto.input.user

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Range

/**
 * Represents an authenticated user.
 */
data class AuthenticatedUserInputModel(
    @get:Range(min = 1)val uId: UInt,
    @get:NotBlank val token: String,
)