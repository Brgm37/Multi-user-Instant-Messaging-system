import {ChannelsState} from "./ChannelsState";

/**
 * ChannelsAction is a discriminated union type that represents the actions that can be performed on the channels view.
 *
 * The actions can be one of the following:
 * - init: Initialize the view.
 * - loadMore: Load more channels at the head or tail.
 * - loadSuccess: Load channels successfully.
 * - loadError: Load channels with an error.
 * - goBack: Go back to the previous state.
 *
 * @type ChannelsAction
 * @prop tag The type of the action.
 * @prop at The position to load more channels.
 * @prop message The error message.
 * @prop previous The previous state of the channels view.
 */
export type ChannelsAction =
    | { tag: "init" }
    | { tag: "loadMore", at: "head" | "tail" }
    | { tag: "loadSuccess" }
    | { tag: "loadError", message: string, previous: ChannelsState }
    | { tag: "goBack" }