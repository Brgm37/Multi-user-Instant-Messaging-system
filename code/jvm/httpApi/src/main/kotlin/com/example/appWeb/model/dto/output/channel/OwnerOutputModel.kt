package com.example.appWeb.model.dto.output.channel

import model.users.UserInfo

/**
 * Represents the owner output model
 *
 * @property id The id of the owner
 * @property name The name of the owner
 */
data class OwnerOutputModel(
    val id: UInt,
    val name: String,
) {
    companion object {
        fun fromDomain(owner: UserInfo): OwnerOutputModel =
            OwnerOutputModel(
                id = owner.uId,
                name = owner.username,
            )
    }
}