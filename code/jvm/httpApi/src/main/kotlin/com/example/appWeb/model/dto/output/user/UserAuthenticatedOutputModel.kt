package com.example.appWeb.model.dto.output.user

import model.users.UserToken

/**
 * Represents the output model for a user authenticated
 * @param uId The user id
 * @param token The token of the user
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