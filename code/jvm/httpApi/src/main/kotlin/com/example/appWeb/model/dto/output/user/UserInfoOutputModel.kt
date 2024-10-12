package com.example.appWeb.model.dto.output.user

import model.User

data class UserInfoOutputModel(
	val username: String,
) {
	companion object {
		fun fromDomain(user: User): UserInfoOutputModel =
			UserInfoOutputModel(
				username = user.username,
			)
	}
}
