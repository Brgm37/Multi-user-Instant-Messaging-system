package com.example.appWeb.model.dto.input.channel

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.NotBlank
import model.channels.AccessControl
import model.channels.Visibility

/**
 * Data class representing the input model for creating a channel.
 *
 * @property name The name of the channel to create.
 * @property visibility The visibility of the channel to create.
 * @property accessControl The access control of the channel to create.
 */
data class CreateChannelInputModel(
    @get:NotBlank val name: String,
    @get:NotBlank val visibility: String,
    @get:NotBlank val accessControl: String,
    val description: String? = null,
    val icon: String? = null,
) {
    @AssertTrue(message = "Invalid visibility")
    @Schema(hidden = true)
    fun isValidVisibility(): Boolean = Visibility.validate(visibility)

    @AssertTrue(message = "Invalid access control")
    @Schema(hidden = true)
    fun isValidAccessControl(): Boolean = AccessControl.validate(accessControl)
}