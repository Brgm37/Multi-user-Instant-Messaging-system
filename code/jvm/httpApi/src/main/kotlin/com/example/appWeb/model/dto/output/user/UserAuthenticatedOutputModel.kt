package com.example.appWeb.model.dto.output.user

import model.users.UserToken

/**
 * Represents the output model for a user authenticated
 *
 * @property uId The user id of the authenticated user
 * @property token The authentication token of the user authenticated
 */
data class UserAuthenticatedOutputModel(
    val uId: UInt,
    val token: String,
) {
    companion object {
        fun fromDomain(token: UserToken): UserAuthenticatedOutputModel =
            UserAuthenticatedOutputModel(
                uId = token.userId,
                token = token.token.toString(),
            )
    }
}