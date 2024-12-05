package com.example.appWeb.model.dto.output.channel

import model.channels.Channel

/**
 * Represents the channel list output model
 *
 * @property name The channel name output model
 * @property id The channel id
 * @property ownerOutputModel The owner output model
 * @property description The description of the channel
 * @property icon The icon of the channel
 */
data class ChannelListOutputModel(
    val name: ChannelNameOutputModel,
    val id: UInt,
    val ownerOutputModel: OwnerOutputModel,
    val description: String? = null,
    val icon: String? = null,
) {
    companion object {
        fun fromDomain(channel: Channel): ChannelListOutputModel {
            val id = checkNotNull(channel.cId) { "Channel id must not be null" }
            return ChannelListOutputModel(
                name = ChannelNameOutputModel.fromDomain(channel.name),
                id = id,
                ownerOutputModel = OwnerOutputModel.fromDomain(channel.owner),
                description = channel.description,
                icon = channel.icon,
            )
        }
    }
}