package com.example.appWeb.model.dto.input.channel

import jakarta.validation.constraints.AssertTrue
import model.channels.AccessControl
import org.hibernate.validator.constraints.Range

/**
 * Data class representing the input model for creating a channel invitation.
 *
 * @property maxUses The maximum number of uses for the invitation.
 * @property expirationDate The expiration date of the invitation.
 * @property accessControl The access control of the invitation.
 */
data class CreateChannelInvitationInputModel(
    @get:Range(min = 1) val maxUses: UInt = 1u,
    @get:Range(min = 1) val owner: UInt,
    val expirationDate: String? = null,
    val accessControl: String = AccessControl.READ_ONLY.name,
) {
    @AssertTrue(message = "Invalid access control")
    fun isValidAccessControl(): Boolean =
        AccessControl
            .entries
            .map(AccessControl::name)
            .contains(accessControl.uppercase())
}