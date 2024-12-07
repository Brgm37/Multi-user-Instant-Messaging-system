import {PublicChannel} from "../../../../model/PublicChannel";

/**
 * The Action type for the PublicChannels form.
 *
 * @type FindChannelAction
 * @prop type The type of the action.
 * @prop channels The list of public channels.
 * @prop error The error message.
 */
export type FindChannelAction =
    | { type: "init" }
    | { type: "error", error: string }
    | { type: "success" }
    | { type: "loadMore", at: "head" | "tail" }
    | { type: "closeError" }
    | { type: "join", channelId: number }
    | { type: "joinSuccess" }