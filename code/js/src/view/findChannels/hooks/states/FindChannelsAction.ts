
/**
 * The Action type for the PublicChannels form.
 *
 * The action can be of the following types:
 * - init: The initial state.
 * - error: An error occurred.
 * - success: The channels were successfully loaded.
 * - loadMore: Load more channels.
 * - closeError: Close the error message.
 * - join: Join a channel.
 * - joinSuccess: The channel was joined successfully.
 *
 * @type FindChannelAction
 * @prop type The type of the action.
 * @prop error The error message.
 * @prop channelId The channel id.
 * @prop at The position to load more channels.
 */
export type FindChannelAction =
    | { type: "init" }
    | { type: "error", error: string }
    | { type: "success" }
    | { type: "loadMore", at: "head" | "tail" }
    | { type: "closeError" }
    | { type: "join", channelId: number }
    | { type: "joinSuccess" }