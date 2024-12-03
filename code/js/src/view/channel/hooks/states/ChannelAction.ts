import {Message} from "../../../../model/Message";
import {Channel} from "../../../../model/Channel";
import {ChannelState} from "./ChannelState";
import {UseScrollState} from "../../../../service/utils/hooks/useScroll/UseScroll";

/**
 *
 * @type ChannelAction
 *
 * @description
 * ChannelAction is a discriminated union type that represents all possible actions
 * that can be dispatched to the channel reducer.
 *
 * @see ChannelState
 */
export type ChannelAction =
    { tag: "init" } |
    { tag: "loadMore", at: "head" | "tail" } |
    { tag: "loadSuccess", messages: UseScrollState<Message> } |
    { tag: "loadError", error: string, previous: ChannelState } |
    { tag: "sendMessage" } |
    { tag: "sendSuccess" } |
    { tag: "sendError", error: string, previous: ChannelState } |
    { tag: "go-back" }