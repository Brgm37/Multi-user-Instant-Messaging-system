
/**
 * ChannelState is a discriminated union type that represents the different states that a channel can be in.
 *
 * The states are:
 * - idle: The channel is in an idle state.
 * - messages: The channel is in a state where messages are being displayed.
 * - loading: The channel is in a loading state.
 * - error: The channel is in an error state.
 *
 * @type ChannelState
 * @prop tag The type of the state.
 * @prop at The position to load more messages.
 * @prop message The error message.
 * @prop previous The previous state of the channel.
 */
export type ChannelState =
    { tag: "idle" } |
    { tag: "messages" } |
    { tag: "loading", at: "head" | "tail" | "sending" | "receiving" | "both" } |
    { tag: "error", message: string, previous: ChannelState }