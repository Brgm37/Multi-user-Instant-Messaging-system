package com.example.appWeb.model.dto.input.channel

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
) {
    @AssertTrue(message = "Invalid visibility")
    fun isValidVisibility(): Boolean = Visibility.validate(visibility)

    @AssertTrue(message = "Invalid access control")
    fun isValidAccessControl(): Boolean = AccessControl.validate(accessControl)
}