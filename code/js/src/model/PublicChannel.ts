import {Channel, jsonToPublicChannel} from "./Channel";

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
    icon: string,
}

/**
 * @description Converts an array of channels to an array of public channels.
 *
 * @param channels The array of channels to convert.
 * @returns PublicChannel[]
 */
export function channelsToPublicChannels(channels: Channel[]): PublicChannel[] {
    return channels.map(jsonToPublicChannel);
}

