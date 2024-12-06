import {jsonToMessage, Message} from "./Message";
import {AccessControl} from "./AccessControl";
import {ChannelVisibility} from "./ChannelVisibility";
import {PublicChannel} from "./PublicChannel";

/**
 * @type Channel
 *
 * @description
 * This type is used to represent the channel object.
 *
 * @property {string} id - The id of the channel.
 * @property {string} name - The name of the channel.
 * @property {Message[]} messages - The messages of the channel.
 */
export type Channel = Identifiable & {
    id: string,
    name: string,
    messages: Message[],
    hasMore: boolean,
    accessControl: AccessControl,
    visibility: ChannelVisibility,
    owner: Owner,
    icon: string,
}

/**
 * @description Owner type.
 *
 * @type Owner
 * @prop id The id of the owner.
 * @prop name The name of the owner.
 */
type Owner = {
    id: number,
    name: string,
}

export function jsonToChannel(json: any): Channel {
    let messages = json.messages
    if (messages !== undefined) messages.map((msg: any) => jsonToMessage(msg))

    return {
        id: json.id,
        name: json.name.displayName as string,
        messages: messages,
        hasMore: json.hasMore,
        accessControl: json.access,
        visibility: json.visibility,
        owner: json.owner,
        icon: json.icon
    }
}

/**
 * @description Converts a channel to a public channel.
 *
 * @param json The object to convert.
 * @returns PublicChannel
 */
export function jsonToPublicChannel(json: any): PublicChannel {
    return {
        id: json.id,
        name: json.name.displayName,
        owner: json.owner.name,
        description: json.description,
        icon: json.icon
    }
}
