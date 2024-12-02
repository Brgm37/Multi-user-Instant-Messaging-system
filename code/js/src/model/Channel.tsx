import {jsonToMessage, Message} from "./Message";
import {UserInfo} from "./UserInfo";

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
    const messages = json.messages
    messages.map((msg: any) => jsonToMessage(msg))
    return {
        id: json.id,
        name: json.name,
        messages: messages,
        hasMore: json.hasMore
    }
}