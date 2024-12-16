package com.example.appWeb.model.dto.input.channel

import org.junit.jupiter.api.Test

class CreateChannelInvitationInputModelTest {
    @Test
    fun `isValidAccessControl should return true for valid access control`() {
        // Given
        val inputModel =
            CreateChannelInvitationInputModel(
                channelId = 1u,
                maxUses = 1u,
                expirationDate = null,
                accessControl = "READ_ONLY",
            )

        // When
        val result = inputModel.isValidAccessControl()

        // Then
        assert(result)
    }

    @Test
    fun `isValidAccessControl should return false for invalid access control`() {
        // Given
        val inputModel =
            CreateChannelInvitationInputModel(
                channelId = 1u,
                maxUses = 1u,
                expirationDate = null,
                accessControl = "INVALID",
            )

        // When
        val result = inputModel.isValidAccessControl()

        // Then
        assert(!result)
    }
}