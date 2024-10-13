package com.example.appWeb.model.dto.output.channel

import com.example.appWeb.model.dto.output.message.MessageOutputModel
import model.channels.Channel
import model.channels.Visibility.PRIVATE
import model.channels.Visibility.PUBLIC

/**
 * Represents a ChannelOutputModel.
 *
 * @property id the channel’s identifier (unique).
 * @property owner the owner of the channel.
 * @property name the name of the channel.
 * @property accessControl the access control settings of the channel.
 * @property visibility the visibility of the channel.
 * @property messages the messages in the channel.
 */
data class ChannelOutputModel(
    val id: UInt,
    val owner: OwnerOutputModel,
    val name: ChannelNameOutputModel,
    val accessControl: String,
    val visibility: String,
    val messages: List<MessageOutputModel> = emptyList(),
) {
    companion object {
        fun fromDomain(channel: Channel): ChannelOutputModel =
            ChannelOutputModel(
                id = requireNotNull(channel.channelId) { "Channel id is null" },
                owner = OwnerOutputModel.fromDomain(channel.owner),
                name = ChannelNameOutputModel.fromDomain(channel.name),
                accessControl = channel.accessControl.name,
                visibility = if (channel is Channel.Public) PUBLIC.name else PRIVATE.name,
                messages = channel.messages.map(MessageOutputModel::fromDomain),
            )
    }
}
