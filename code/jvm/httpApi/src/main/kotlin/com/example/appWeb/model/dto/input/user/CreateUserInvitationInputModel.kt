package com.example.appWeb.model.dto.input.user

import jakarta.validation.constraints.Pattern

class CreateUserInvitationInputModel {
    @get:Pattern(
        regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}",
        message = "Invalid date format, expected YYYY-MM-DDTHH:MM:SS",
    )
    val expirationDate: String? = null
}