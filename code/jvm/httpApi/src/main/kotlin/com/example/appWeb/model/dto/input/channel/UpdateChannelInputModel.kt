package com.example.appWeb.model.dto.input.channel

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.AssertTrue
import model.channels.AccessControl
import model.channels.Visibility

data class UpdateChannelInputModel(
    val name: String? = null,
    val visibility: String? = null,
    val accessControl: String? = null,
    val description: String? = null,
    val icon: String? = null,
) {
    @AssertTrue(message = "Invalid visibility")
    @Schema(hidden = true)
    fun isValidVisibility(): Boolean = visibility?.let { Visibility.validate(it) } ?: true

    @AssertTrue(message = "Invalid access control")
    @Schema(hidden = true)
    fun isValidAccessControl(): Boolean = accessControl?.let { AccessControl.validate(it) } ?: true

    @AssertTrue(message = "Invalid icon")
    @Schema(hidden = true)
    fun isValidIcon(): Boolean = icon?.isNotBlank() ?: true
}