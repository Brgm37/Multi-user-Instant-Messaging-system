package com.example.appWeb.model.dto.input.channel

import org.junit.jupiter.api.Test

class CreateChannelInputModelTest {
    @Test
    fun `isValidVisibility should return true for valid visibility`() {
        // Given
        val inputModel =
            CreateChannelInputModel(
                name = "name",
                visibility = "PUBLIC",
                accessControl = "READ_WRITE",
                description = null,
                icon = null,
            )

        // When
        val result = inputModel.isValidVisibility()

        // Then
        assert(result)
    }

    @Test
    fun `isValidVisibility should return false for invalid visibility`() {
        // Given
        val inputModel =
            CreateChannelInputModel(
                name = "name",
                visibility = "INVALID",
                accessControl = "READ_WRITE",
                description = null,
                icon = null,
            )

        // When
        val result = inputModel.isValidVisibility()

        // Then
        assert(!result)
    }

    @Test
    fun `isValidAccessControl should return true for valid access control`() {
        // Given
        val inputModel =
            CreateChannelInputModel(
                name = "name",
                visibility = "PUBLIC",
                accessControl = "READ_ONLY",
                description = null,
                icon = null,
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
            CreateChannelInputModel(
                name = "name",
                visibility = "PUBLIC",
                accessControl = "INVALID",
                description = null,
                icon = null,
            )

        // When
        val result = inputModel.isValidAccessControl()

        // Then
        assert(!result)
    }
}