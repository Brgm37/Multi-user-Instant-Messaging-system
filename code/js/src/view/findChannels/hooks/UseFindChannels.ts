import {FindChannelState} from "./states/FindChannelsState";
import {useContext, useEffect, useReducer, useRef, useState} from "react";
import {UseFindChannelsHandler} from "./handler/UseFindChannelsHandler";
import envConfig from "../../../../envConfig.json"
import reduce from "./reducer/FindChannelReducer";
import {FindChannelsServiceContext} from "../../../service/findChannels/FindChannelsServiceContext";
import useScroll, {HasMore, UseScrollHandler, UseScrollState} from "../../../service/utils/hooks/useScroll/UseScroll";
import {PublicChannel} from "../../../model/PublicChannel";

const LIST_SIZE = envConfig.channels_limit
const DEFAULT_LIMIT = envConfig.default_limit
let limit = DEFAULT_LIMIT
const HAS_MORE = DEFAULT_LIMIT + 1

/**
 * The number of channels to fetch per request.
 */
const CHANNELS_PER_FETCH = envConfig.public_channels_limit

function resetList(
    listHandler: UseScrollHandler<PublicChannel>,
    result: PublicChannel[],
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
    listHandler: UseScrollHandler<PublicChannel>,
    list: UseScrollState<PublicChannel>,
    result: PublicChannel[],
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

export function useFindChannels(): [FindChannelState, UseScrollState<PublicChannel>, string, UseFindChannelsHandler] {
    const [list, listHandler] = useScroll<PublicChannel>(LIST_SIZE)
    const [searchValue, setSearchValue] = useState<string>("")
    const initialState: FindChannelState = {tag: "idle"}
    const [state, dispatch] = useReducer(reduce, initialState)
    const {getChannelsByPartialName, getPublicChannels, joinChannel} = useContext(FindChannelsServiceContext)
    const wasMounted = useRef(false)

    useEffect(() => {
        if (!wasMounted.current) {
            wasMounted.current = true
            return
        }
        if (state.tag === "idle") return
        if (state.tag === "error") return
        if (searchValue === "") {
            getPublicChannels(0, HAS_MORE)
                .then((response) => {
                    if (response.tag === "success") resetList(listHandler, response.value)
                    else dispatch({type: "error", error: response.value})
                })
        } else {
            getChannelsByPartialName(searchValue, 0, HAS_MORE)
                .then((response) => {
                    if (response.tag === "success") resetList(listHandler, response.value)
                    else dispatch({type: "error", error: response.value})
                })
        }
    }, [searchValue]);

    useEffect(() => {
        if (state.tag === "idle") return
        if (state.tag === "error") return
        if (state.tag === "scrolling") return
        if (state.tag === "loading") dispatch({type: "success"})
    }, [list]);

    const onInit = () => {
        if (state.tag != "idle") return
        dispatch({type: "init"})
        getPublicChannels(0, HAS_MORE)
            .then((response) => {
                if (response.tag === "success") resetList(listHandler, response.value)
                else dispatch({type: "error", error: response.value})
            })
    }

    const onSearchChange = (searchBar: string) => {
        setSearchValue(searchBar)
    }

    const onFetchMore = (offset: number, at: "head" | "tail") => {
        if (state.tag != "scrolling") return
        if (at === "head" && !list.hasMore.head) return
        if (at === "tail" && !list.hasMore.tail) return
        if (searchValue === "") {
            getPublicChannels(offset*limit, CHANNELS_PER_FETCH)
                .then((response) => {
                    if (response.tag === "success") addItems(at, offset, listHandler, list, response.value)
                    else dispatch({type: "error", error: response.value})
                })
        } else {
            getChannelsByPartialName(searchValue, offset*limit, CHANNELS_PER_FETCH)
                .then((response) => {
                    if (response.tag === "success") addItems(at, offset, listHandler, list, response.value)
                    else dispatch({type: "error", error: response.value})
                })
        }
        dispatch({type: "loadMore", at: "tail"})
    }

    const onErrorClose = () => {
        if (state.tag != "error") return
        dispatch({type: "closeError"})
    }

    const onJoin = (channelId: number) => {
        if (state.tag !== "scrolling") return
        joinChannel(channelId)
            .then((response) => {
                if (response.tag === "success") dispatch({type: "joinSuccess"})
                else dispatch({type: "error", error: response.value})
            })
        dispatch({type: "join", channelId: channelId})
    }

    const handler: UseFindChannelsHandler = {
        onInit,
        onSearchChange,
        onFetchMore,
        onErrorClose,
        onJoin
    }

    return [state, list, searchValue,handler]
}





















