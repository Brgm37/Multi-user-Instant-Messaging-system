/**
 * @description Type for the state of the PublicChannels form.
 * 
 * @type PublicChannel
 * @prop id The id of the public channel.
 * @prop name The name of the public channel.
 */
export type PublicChannel = {
    id: number,
    name: string,
    owner: string,
}

/**
 * @description Channel type.
 *
 * @type Channel
 * @prop name The name of the channel.
 * @prop id The id of the channel.
 * @prop owner The owner of the channel.
 */
export type Channel = {
    name: ChannelName,
    id: number,
    owner: Owner,
}

/**
 * @description Channel name type.
 *
 * @type ChannelName
 * @prop name The name of the channel.
 * @prop displayName The display name of the channel.
 */
export type ChannelName = {
    name: string,
    displayName: string,
}

/**
 * @description Owner type.
 *
 * @type Owner
 * @prop id The id of the owner.
 * @prop name The name of the owner.
 */
export type Owner = {
    id: number,
    name: string,
}

/**
 * @description Converts a channel to a public channel.
 *
 * @param channel The channel to convert.
 * @returns PublicChannel
 */
export function channelToPublicChannel(channel: Channel): PublicChannel {
    return {
        id: channel.id,
        name: channel.name.displayName,
        owner: channel.owner.name,
    }
}

/**
 * @description Converts an array of channels to an array of public channels.
 *
 * @param channels The array of channels to convert.
 * @returns PublicChannel[]
 */
export function channelsToPublicChannels(channels: Channel[]): PublicChannel[] {
    return channels.map(channelToPublicChannel);
}

