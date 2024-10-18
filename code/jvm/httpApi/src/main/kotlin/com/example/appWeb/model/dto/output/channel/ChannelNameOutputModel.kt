package com.example.appWeb.model.dto.output.channel

import model.channels.ChannelName

/**
 * Represents the channel name output model
 *
 * @property name The full name of the channel
 * @property displayName The display name of the channel
 */
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