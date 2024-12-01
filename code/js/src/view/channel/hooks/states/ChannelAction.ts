import {Message} from "../../../../model/Message";
import {Channel} from "../../../../model/Channel";
import {ChannelState} from "./ChannelState";

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
    { type: "startUp" } |
    { type: "loaded-channel", channel: Channel } |
    { type: "error", error: string, previous: ChannelState } |
    { type: "go-back" } |
    { type: "load-more" } |
    { type: "loaded-more", messages: Message[], hasMore: boolean } |
    { type: "send-msg", message: Message } |
    { type: "mag-sent" }