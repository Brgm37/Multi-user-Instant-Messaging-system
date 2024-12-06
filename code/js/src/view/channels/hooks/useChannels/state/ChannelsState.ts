/**
 * ChannelsState is a discriminated union type that represents the state of the channels view.
 *
 * The state can be in one of the following states:
 * - idle: The view is in the initial state and the channels are being loaded.
 * - loading: The view is loading the channels. The loading can be at the head, tail or both.
 * - scrolling: The view is scrolling.
 * - error: The view has encountered an error and the message is displayed.
 *
 */
export type ChannelsState =
    { tag: "idle" } |
    { tag: "loading", at: "head" | "tail" | "both"} |
    { tag: "scrolling", } |
    { tag: "error", message: string, previous: ChannelsState }