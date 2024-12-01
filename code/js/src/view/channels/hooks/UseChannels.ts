import * as React from "react";
import {ChannelsState} from "./state/ChannelsState";
import {ChannelsAction} from "./state/ChannelsAction";
import {useReducer} from "react";
import {ChannelHandler} from "./handler/ChannelHandler";

function reduce(state: ChannelsState, action: ChannelsAction): ChannelsState {
    switch (state.tag) {
        case "idle":
            switch (action.tag) {
                case "init":
                    return {tag: "loading", channels: []}
                default:
                    throw Error(`Action ${action.tag} is not allowed in state ${state.tag}`)
            }
        case "loading":
            switch (action.tag) {
                case "loadSuccess":
                    return {tag: "channels", channels: action.channels, hasMore: action.hasMore}
                case "loadError":
                    return {tag: "error", message: action.message, previous: action.previous}
                default:
                    throw Error(`Action ${action.tag} is not allowed in state ${state.tag}`)
            }
        case "channels":
            switch (action.tag) {
                case "search":
                    return {tag: "searching", query: action.query}
                case "loadMore":
                    return {tag: "loading", channels: state.channels}
                default:
                    throw Error(`Action ${action.tag} is not allowed in state ${state.tag}`)
            }
        case "searching":
            switch (action.tag) {
                case "searchSuccess":
                    return {tag: "searchResults", channels: action.channels, hasMore: action.hasMore}
                case "searchError":
                    return {tag: "error", message: action.message, previous: state}
                default:
                    throw Error(`Action ${action.tag} is not allowed in state ${state.tag}`)
            }
        case "searchResults":
            switch (action.tag) {
                case "loadMore":
                    return {tag: "loading", channels: state.channels}
                case "search":
                    return {tag: "searching", query: action.query}
                case "clear":
                    return {tag: "channels", channels: state.channels, hasMore: state.hasMore}
                default:
                    throw Error(`Action ${action.tag} is not allowed in state ${state.tag}`)
            }
        case "error":
            switch (action.tag) {
                case "goBack":
                    return state.previous
                default:
                    throw Error(`Action ${action.tag} is not allowed in state ${state.tag}`)
            }
    }
}

export default function (): [ChannelsState, ChannelHandler] {
    const [state, dispatch] = useReducer(reduce, {tag: "idle"})
    const handler: ChannelHandler = {
        clear(): void {
        }, goBack(): void {
        }, loadChannels(): void {
        }, loadMore(): void {
        }, search(query: string): void {
        }
    }
    return [state, handler]
}