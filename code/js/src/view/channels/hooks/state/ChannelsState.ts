import {UseScrollState} from "../../../../service/utils/hooks/useScroll/UseScroll";
import {Channel} from "../../../../model/Channel";

export type ChannelsState =
    { tag: "idle", channels: UseScrollState<Channel> } |
    { tag: "loading", channels: UseScrollState<Channel>, at: "head" | "tail" | "both"} |
    { tag: "channels", channels: UseScrollState<Channel> } |
    { tag: "error", message: string, previous: ChannelsState } |
    { tag: "searching", query: string, channels: UseScrollState<Channel> } |
    { tag: "searchResults", channels: UseScrollState<Channel>, query: string }