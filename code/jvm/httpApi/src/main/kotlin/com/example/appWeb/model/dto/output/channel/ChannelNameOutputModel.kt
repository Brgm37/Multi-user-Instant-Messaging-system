package com.example.appWeb.model.dto.output.channel

import model.channels.ChannelName

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