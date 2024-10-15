package com.example.appWeb.model.dto.input.message

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

/**
 * Data class representing the input model for creating a message.
 */
data class CreateMessageInputModel(
    @get:NotBlank val msg: String,
    @get:Positive val user: UInt,
    @get:Positive val channel: UInt,
)