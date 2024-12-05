package com.example.appWeb.model.dto.input.channel

import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.NotBlank
import model.channels.AccessControl
import model.channels.Visibility

data class UpdateChannelInputModel(
    @get:NotBlank val name: String? = null,
    @get:NotBlank val visibility: String? = null,
    @get:NotBlank val accessControl: String? = null,
    val description: String? = null,
    @get:NotBlank val icon: String? = null,
){
    @AssertTrue(message = "Invalid visibility")
    fun isValidVisibility(): Boolean = visibility?.let { Visibility.validate(it) } ?: true

    @AssertTrue(message = "Invalid access control")
    fun isValidAccessControl(): Boolean = accessControl?.let { AccessControl.validate(it) } ?: true

    @AssertTrue(message = "Invalid description")
    fun isValidDescription(): Boolean = description?.isNotBlank() ?: true

    @AssertTrue(message = "Invalid icon")
    fun isValidIcon(): Boolean = icon?.isNotBlank() ?: true
}