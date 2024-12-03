import {PublicChannel} from "../../../../model/PublicChannel";

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
    { type: "error", error: string} |
    { type: "success", channels: PublicChannel[] } |
    { type: "fetchMore" } |
    { type: "closeError" } |
    { type: "join", channelId: number } |
    { type: "joined" } |
    { type: "edit", inputName: "searchBar", inputValue: string }