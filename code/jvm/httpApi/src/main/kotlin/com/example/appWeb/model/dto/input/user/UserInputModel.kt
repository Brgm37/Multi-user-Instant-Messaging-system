package com.example.appWeb.model.dto.input.user

import jakarta.annotation.PostConstruct
import model.Password

/**
 * Represents the input model for a user
 * @param username The username
 * @param password The password
 */
data class UserInputModel(
	val username: String,
	val password: Password,
) {
	@PostConstruct
	fun validate() {
		require(username.isNotBlank()) { "Username cannot be blank" }
	}
}