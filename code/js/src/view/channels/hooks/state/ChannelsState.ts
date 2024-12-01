import {Channel} from "../../../../model/Channel";

export type ChannelsState =
    { tag: "idle" } |
    { tag: "loading", channels: Channel[] } |
    { tag: "channels", channels: Channel[], hasMore: boolean } |
    { tag: "error", message: string, previous: ChannelsState } |
    { tag: "searching", query: string } |
    { tag: "searchResults", channels: Channel[], hasMore: boolean }