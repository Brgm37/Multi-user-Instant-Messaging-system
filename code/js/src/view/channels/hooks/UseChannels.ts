import {ChannelsState} from "./state/ChannelsState";
import {ChannelsAction} from "./state/ChannelsAction";
import {useContext, useEffect, useReducer} from "react";
import {ChannelsHandler} from "./handler/ChannelsHandler";
import {ChannelsServiceContext} from "../../../service/channels/ChannelsServiceContext";
import useScroll, {HasMore, UseScrollHandler, UseScrollState} from "../../../service/utils/hooks/useScroll/UseScroll";
import {Channel} from "../../../model/Channel";
import envConfig from "../../../../envConfig.json"
import {Either} from "../../../model/Either";

const LIST_SIZE = envConfig.channels_limit
const DEFAULT_LIMIT = envConfig.default_limit
let limit = DEFAULT_LIMIT
const HAS_MORE = DEFAULT_LIMIT + 1

function reduce(state: ChannelsState, action: ChannelsAction): ChannelsState {
    switch (state.tag) {
        case "idle":
            switch (action.tag) {
                case "init":
                    return {tag: "loading", channels: state.channels, at: "both"}
                default:
                    throw Error(`Action ${action.tag} is not allowed in state ${state.tag}`)
            }
        case "loading":
            switch (action.tag) {
                case "loadSuccess":
                    return {...state, tag: "channels", channels: action.channels}
                case "loadError":
                    return {tag: "error", message: action.message, previous: action.previous}
                default:
                    throw Error(`Action ${action.tag} is not allowed in state ${state.tag}`)
            }
        case "channels":
            switch (action.tag) {
                case "search":
                    return {tag: "searching", query: action.query, channels: state.channels}
                case "loadMore":
                    return {tag: "loading", channels: state.channels, at: action.at}
                default:
                    throw Error(`Action ${action.tag} is not allowed in state ${state.tag}`)
            }
        case "searching":
            switch (action.tag) {
                case "searchSuccess":
                    return {...state, tag: "searchResults", channels: action.channels}
                case "searchError":
                    return {tag: "error", message: action.message, previous: state}
                case "clear":
                    return {tag: "loading", channels: state.channels, at: "both"}
                default:
                    throw Error(`Action ${action.tag} is not allowed in state ${state.tag}`)
            }
        case "searchResults":
            switch (action.tag) {
                case "loadMore":
                    return {tag: "loading", channels: state.channels, at: action.at}
                case "search":
                    return {tag: "searching", query: action.query, channels: state.channels}
                case "clear":
                    return {tag: "loading", channels: state.channels, at: "both"}
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

function resetList(
    listHandler: UseScrollHandler<Channel>,
    result: Channel[],
): void {
    const channels = result.slice(0, limit)
    const hasMore = {head: false, tail: result.length === HAS_MORE}
    listHandler.reset(channels, hasMore)
}

function addItems(
    at: "head" | "tail",
    offset: number,
    listHandler: UseScrollHandler<Channel>,
    list: UseScrollState<Channel>,
    result: Channel[],
): void {
    const channels = result.slice(0, limit)
    if (channels.length < limit) limit = channels.length
    else limit = DEFAULT_LIMIT
    const tail =
        result.length === HAS_MORE && at === "tail" ||
        at === "head" && list.list.length === LIST_SIZE
    const hasMore: HasMore = {
        head: offset > 0 && list.list.length === LIST_SIZE,
        tail
    }
    listHandler.addItems(channels, at, hasMore)
}

export default function (): [ChannelsState, ChannelsHandler] {
    const [list, listHandler] = useScroll<Channel>(LIST_SIZE)
    const initialState: ChannelsState = {tag: "idle", channels: list}
    const [state, dispatch] = useReducer(reduce, initialState)
    const service = useContext(ChannelsServiceContext)
    useEffect(() => {
        if (state.tag === "error") return
        if (state.tag === "searching") {
            if (state.channels !== list) {
                dispatch({tag: "searchSuccess", channels: list});
            }
        } else {
            if (state.channels !== list) {
                dispatch({tag: "loadSuccess", channels: list});
            }
        }
    }, [list]);
    const handler: ChannelsHandler = {
        clear(): void {
            if (state.tag === "channels") return
            service.findChannels(0, HAS_MORE)
                .then(result => {
                    if (result.tag === "success") resetList(listHandler, result.value)
                    else dispatch({tag: "loadError", message: result.value, previous: state})
                })
            dispatch({tag: "clear"})
        },
        goBack(): void {
            dispatch({tag: "goBack"})
        },
        loadChannels(): void {
            service.findChannels(0, HAS_MORE)
                .then(result => {
                    if (result.tag === "success") resetList(listHandler, result.value)
                    else dispatch({tag: "loadError", message: result.value, previous: state})
                })
            dispatch({tag: "init"})
        },
        loadMore(offset, at): void {
            if (state.tag === "loading") return
            service.findChannels(offset * limit, HAS_MORE)
                .then(result => {
                    if (result.tag === "success") addItems(at, offset, listHandler, list, result.value)
                    else dispatch({tag: "loadError", message: result.value, previous: state})
                })
            dispatch({tag: "loadMore", at})

        },
        search(query: string): void {
            service.findChannelsByName(query, 0, HAS_MORE)
                .then(result => {
                    if (result.tag === "success") resetList(listHandler, result.value)
                    else dispatch({tag: "searchError", message: result.value, previous: state})
                })
            dispatch({tag: "search", query})
        },
        searchMore(query: string, offset: number, at): void {
            service.findChannelsByName(query, offset, HAS_MORE)
                .then(result => {
                    if (result.tag === "success") addItems(at, offset, listHandler, list, result.value)
                    else dispatch({tag: "searchError", message: result.value, previous: state})
                })
            dispatch({tag: "loadMore", at})
        },
    }
    return [state, handler]
}