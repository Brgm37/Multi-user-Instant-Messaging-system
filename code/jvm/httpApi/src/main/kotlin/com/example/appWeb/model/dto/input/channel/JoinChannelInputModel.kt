package com.example.appWeb.model.dto.input.channel

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.AssertTrue
import org.hibernate.validator.constraints.Range

/**
 * Data class representing the input model for joining a channel.
 *
 * @property cId The id of the channel to join.
 * @property invitationCode The invitation code of the channel to join.
 */
data class JoinChannelInputModel(
    @get:Range(min = 1) val cId: UInt? = null,
    val invitationCode: String? = null,
) {
    @AssertTrue(message = "The channel id or the invitation code must be provided")
    @Schema(hidden = true)
    fun isValid(): Boolean = cId != null || invitationCode != null
}