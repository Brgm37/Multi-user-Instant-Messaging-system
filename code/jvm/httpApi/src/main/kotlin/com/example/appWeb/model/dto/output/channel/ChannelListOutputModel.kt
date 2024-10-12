package com.example.appWeb.model.dto.output.channel

import model.Channel

/**
 * Represents the channel list output model
 *
 * @property name The channel name output model
 * @property id The channel id
 * @property ownerOutputModel The owner output model
 */
data class ChannelListOutputModel(
	val name: ChannelNameOutputModel,
	val id: UInt,
	val ownerOutputModel: OwnerOutputModel,
) {
	companion object {
		fun fromDomain(channel: Channel): ChannelListOutputModel {
			val id = checkNotNull(channel.channelId) { "Channel id must not be null" }
			return ChannelListOutputModel(
				name = ChannelNameOutputModel.fromDomain(channel.name),
				id = id,
				ownerOutputModel = OwnerOutputModel.fromDomain(channel.owner),
			)
		}
	}
}
