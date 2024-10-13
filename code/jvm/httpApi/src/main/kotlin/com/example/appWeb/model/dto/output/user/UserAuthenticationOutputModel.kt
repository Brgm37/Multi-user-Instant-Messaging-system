package com.example.appWeb.model.dto.output.user

import model.users.User
import java.util.UUID

/**
 * Represents the output model for a user authentication
 * @param uId The user id of the user
 * @param token The token of the user
 */
data class UserAuthenticationOutputModel(
	val uId: UInt,
	val token: UUID,
) {
	companion object {
		fun fromDomain(user: User): UserAuthenticationOutputModel =
			UserAuthenticationOutputModel(
				uId = user.uId ?: throw IllegalArgumentException("User id is null"),
				token = user.token,
			)
	}
}
