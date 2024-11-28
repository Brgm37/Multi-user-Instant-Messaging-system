import {PublicChannel} from "../../model/findChannels/PublicChannel";

/**
 * The Action type for the PublicChannels form.
 *
 * @type Action
 * @prop type The type of the action.
 * @prop channels The list of public channels.
 * @prop error The error message.
 * @prop searchBar The search bar input.
 */
export type Action =
    { type: "search", channels: PublicChannel[] } |
    { type: "error", error: string} |
    { type: "success", channels: PublicChannel[] } |
    { type: "fetchMore", channels: PublicChannel[] } |
    { type: "closeError" } |
    { type: "join", channelId: number } |
    { type: "joined" }