import {Message} from "../../../../model/Message";

/**
 * @type ChannelState
 *
 * @description
 * This type is used to represent the state of the channel component.
 */
export type ChannelState =
    { tag: "idle" } |
    { tag: "init" } |
    { tag: "loaded", messages: Message[], hasMore: boolean } |
    { tag: "error", error: string, previous: ChannelState } |
    { tag: "loading", messages: Message[], hasMore: boolean, intent: ("loadMore" | "sendMessage")[] }

/**
 * The function that returns the initial state.
 *
 * @return [ChannelState]
 */
export function makeChannelInitialState(): ChannelState {
    return {tag: "idle"}
}