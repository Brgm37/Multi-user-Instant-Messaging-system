package com.example.appWeb.model.dto.input.message

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

/**
 * Data class representing the input model for creating a message.
 *
 * @property msg The message to create.
 * @property user The user that creates the message.
 * @property channel The channel where the message is created.
 */
data class CreateMessageInputModel(
    @get:NotBlank val msg: String,
    @get:Positive val channel: UInt,
)