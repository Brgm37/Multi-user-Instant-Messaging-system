package com.example.appWeb.model.dto

import model.ChannelName

data class ChannelNameOutputModel(
	val name: String,
	val displayName: String,
) {
	companion object {
		fun fromDomain(channelName: ChannelName): ChannelNameOutputModel =
			ChannelNameOutputModel(
				name = channelName.fullName,
				displayName = channelName.name,
			)
	}
}
