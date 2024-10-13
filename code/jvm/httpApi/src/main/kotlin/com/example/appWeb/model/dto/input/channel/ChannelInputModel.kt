package com.example.appWeb.model.dto.input.channel

import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import model.AccessControl
import model.Visibility

/**
 * Data class representing the input model for creating a channel.
 *
 * @property owner The id of the user creating the channel.
 * @property name The name of the channel.
 * @property visibility The visibility of the channel.
 * @property accessControl The access control of the channel.
 */
data class ChannelInputModel(
	@get:Positive val owner: UInt,
	@get:NotBlank val name: String,
	@get:NotBlank val visibility: String,
	@get:NotBlank val accessControl: String,
) {
	@AssertTrue(message = "Invalid visibility")
	fun isValidVisibility(): Boolean =
		Visibility
			.entries
			.map(Visibility::name)
			.contains(visibility.uppercase())

	@AssertTrue(message = "Invalid access control")
	fun isValidAccessControl(): Boolean =
		AccessControl
			.entries
			.map(AccessControl::name)
			.contains(accessControl.uppercase())
}
