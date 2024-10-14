package com.example.appWeb.model.dto.output.user

import model.users.User

/**
 * Represents the output model for a user
 * @param username The username of the user
 */
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