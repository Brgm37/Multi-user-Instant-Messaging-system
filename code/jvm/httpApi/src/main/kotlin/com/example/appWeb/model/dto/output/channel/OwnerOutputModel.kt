package com.example.appWeb.model.dto.output.channel

import model.UserInfo

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
