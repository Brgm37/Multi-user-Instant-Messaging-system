package com.example.appWeb.model.dto.output.user

import model.users.User

/**
 * Represents the output model for a user sign up
 * @param uId The user id
 */
data class UserSignUpOutputModel(
    val uId: UInt,
) {
    companion object {
        fun fromDomain(user: User): UserSignUpOutputModel =
            UserSignUpOutputModel(
                uId = user.uId ?: throw IllegalArgumentException("User id is null"),
            )
    }
}