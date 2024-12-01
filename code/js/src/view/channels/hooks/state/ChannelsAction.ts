import {Channel} from "../../../../model/Channel";
import {ChannelsState} from "./ChannelsState";

export type ChannelsAction =
    { tag: "init" } |
    { tag: "loadMore" } |
    { tag: "loadSuccess", channels: Channel[], hasMore: boolean } |
    { tag: "loadError", message: string, previous: ChannelsState } |
    { tag: "search", query: string } |
    { tag: "searchSuccess", channels: Channel[], hasMore: boolean } |
    { tag: "searchError", message: string, previous: ChannelsState } |
    { tag: "clear" } |
    { tag: "goBack"}