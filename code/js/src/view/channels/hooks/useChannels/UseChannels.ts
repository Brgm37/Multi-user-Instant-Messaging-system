import {ChannelsState} from "./state/ChannelsState";
import {useContext, useEffect, useReducer} from "react";
import {ChannelsHandler} from "./handler/ChannelsHandler";
import {ChannelsServiceContext} from "../../../../service/channels/ChannelsServiceContext";
import useScroll, {
    HasMore,
    UseScrollHandler,
    UseScrollState
} from "../../../../service/utils/hooks/useScroll/UseScroll";
import {Channel} from "../../../../model/Channel";
import envConfig from "../../../../../envConfig.json"
import reduce from "./reducer/UseChannelsReducer";

const LIST_SIZE = envConfig.channels_limit
const DEFAULT_LIMIT = envConfig.default_limit
let limit = DEFAULT_LIMIT
const HAS_MORE = DEFAULT_LIMIT + 1

function resetList(
    listHandler: UseScrollHandler<Channel>,
    result: Channel[],
): void {
    const channels = result.slice(0, limit)
    if (channels.length < limit) limit = channels.length
    else limit = DEFAULT_LIMIT
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

export default function (): [ChannelsState, UseScrollState<Channel>, ChannelsHandler] {
    const [list, listHandler] = useScroll<Channel>(LIST_SIZE)
    const initialState: ChannelsState = {tag: "idle"}
    const [state, dispatch] = useReducer(reduce, initialState)
    const {findChannels} = useContext(ChannelsServiceContext)

    useEffect(() => {
        if (state.tag === "idle") return
        if (state.tag === "scrolling") return
        if (state.tag === "error") return
        if (state.tag === "loading") dispatch({tag: "loadSuccess"})
    }, [list]);

    const handler: ChannelsHandler = {
        goBack(): void {
            if (state.tag !== "error") return
            dispatch({tag: "goBack"})
        },
        loadChannels(): void {
            if (state.tag !== "idle") return
            findChannels(0, HAS_MORE)
                .then(result => {
                    if (result.tag === "success") resetList(listHandler, result.value)
                    else dispatch({tag: "loadError", message: result.value, previous: state})
                })
            dispatch({tag: "init"})
        },
        loadMore(offset, at): void {
            if (state.tag !== "scrolling") return
            if (at === "head" && !list.hasMore.head || at === "tail" && !list.hasMore.tail) return
            findChannels(offset * limit, HAS_MORE)
                .then(result => {
                    if (result.tag === "success") addItems(at, offset, listHandler, list, result.value)
                    else dispatch({tag: "loadError", message: result.value, previous: state})
                })
            dispatch({tag: "loadMore", at})

        },
    }
    return [state, list, handler]
}