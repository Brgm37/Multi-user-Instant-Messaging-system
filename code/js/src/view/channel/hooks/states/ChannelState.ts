import {Message} from "../../../../model/Message";
import {UseScrollState} from "../../../../service/utils/hooks/useScroll/UseScroll";

/**
 * @type ChannelState
 *
 * @description
 * This type is used to represent the state of the channel component.
 */
export type ChannelState =
    { tag: "idle", messages: UseScrollState<Message> } |
    { tag: "messages", messages: UseScrollState<Message> } |
    { tag: "loading", messages: UseScrollState<Message>, at: "head" | "tail" | "sending" } |
    { tag: "error", message: string, previous: ChannelState }