package com.example.appWeb.model.dto.output.channel

import model.Channel
import model.Visibility.PRIVATE
import model.Visibility.PUBLIC

data class ChannelOutputModel(
	val id: UInt,
	val owner: OwnerOutputModel,
	val name: ChannelNameOutputModel,
	val accessControl: String,
	val visibility: String,
) {
	companion object {
		fun fromDomain(channel: Channel): ChannelOutputModel =
			ChannelOutputModel(
				id = channel.channelId ?: throw IllegalArgumentException("Channel id is null"),
				owner = OwnerOutputModel.fromDomain(channel.owner),
				name = ChannelNameOutputModel.fromDomain(channel.name),
				accessControl = channel.accessControl.name,
				visibility = if (channel is Channel.Public) PUBLIC.name else PRIVATE.name,
			)
	}
}
