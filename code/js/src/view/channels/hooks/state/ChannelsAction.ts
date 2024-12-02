import {Channel} from "../../../../model/Channel";
import {ChannelsState} from "./ChannelsState";
import {UseScrollState} from "../../../../service/utils/hooks/useScroll/UseScroll";

export type ChannelsAction =
    { tag: "init" } |
    { tag: "loadMore", at: "head" | "tail" } |
    { tag: "loadSuccess", channels: UseScrollState<Channel> } |
    { tag: "loadError", message: string, previous: ChannelsState } |
    { tag: "search", query: string } |
    { tag: "searchSuccess", channels: UseScrollState<Channel> } |
    { tag: "searchError", message: string, previous: ChannelsState } |
    { tag: "clear" } |
    { tag: "goBack"}