import {ChannelState} from "./ChannelState";

/**
 * ChannelAction is a discriminated union type that represents the different actions that can be performed on a channel.
 *
 * The actions can be one of the following:
 * - init: Initialize the channel.
 * - loadMore: Load more messages at the head or tail.
 * - loadSuccess: Load messages successfully.
 * - loadError: Load messages with an error.
 * - sendMessage: Send a message.
 * - sendSuccess: Send a message successfully.
 * - sendError: Send a message with an error.
 * - go-back: Go back to the previous state.
 * - receiving-sse: Receiving messages from the server.
 */
export type ChannelAction =
    { tag: "init" } |
    { tag: "loadMore", at: "head" | "tail" } |
    { tag: "loadSuccess" } |
    { tag: "loadError", error: string, previous: ChannelState } |
    { tag: "sendMessage" } |
    { tag: "sendSuccess" } |
    { tag: "sendError", error: string, previous: ChannelState } |
    { tag: "receiving-sse" } |
    { tag: "reload" } |
    { tag: "go-back" } |
    { tag: "reset"} |
    { tag: "error", error: string}