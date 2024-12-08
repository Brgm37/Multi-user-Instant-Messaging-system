import {UserInfo} from "./UserInfo";

/**
 * @type Message
 *
 * @description
 * This type is used to represent the message object.
 *
 * @property {string} id - The id of the message.
 * @property {string} text - The text of the message.
 * @property {string} owner - The owner of the message.
 * @property {number} timestamp - The timestamp of the message.
 * @property {number} timestamp - The timestamp of the message.
 */
export type Message = Identifiable & {
    id: string,
    text: string,
    channel: string,
    owner: UserInfo,
    timestamp: string,
}

export function jsonToMessage(json: any): Message {
    return {
        id: json.id,
        text: json.message,
        channel: json.channel,
        owner: { id: json.user, username: json.username } as UserInfo,
        timestamp: json.creationTime,
    }
}

/**
 * Compare two messages by their timestamps.
 *
 *
 * @param a
 * @param b
 *
 * @returns number
 */
export function compareTimestamps(a: Message, b: Message): number {
    return a.timestamp.localeCompare(b.timestamp)
}