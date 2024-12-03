import {jsonToMessage, Message} from "./Message";

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
}

export function jsonToChannel(json: any): Channel {
    let messages = json.messages
    let hasMore = json.hasMore
    if (hasMore === undefined) hasMore = false
    if (messages === undefined) messages = []
    else messages.map((msg: any) => jsonToMessage(msg))
    return {
        id: json.id,
        name: json.name.displayName as string,
        messages: messages,
        hasMore: hasMore,
    }
}