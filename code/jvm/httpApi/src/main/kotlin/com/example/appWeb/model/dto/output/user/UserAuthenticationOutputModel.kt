package com.example.appWeb.model.dto.output.user

import model.User
import java.util.UUID

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
