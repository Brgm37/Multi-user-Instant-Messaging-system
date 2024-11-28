import {Action} from "./states/FindChannelsAction";
import {State} from "./states/FindChannelsState";
import {useEffect, useReducer} from "react";
import {useFetch} from "../../../service/utils/useFetch";
import {channelsToPublicChannels} from "../model/findChannels/PublicChannel";
import {makeDefaultFindChannelService, FindChannelsService} from "../../../service/findChannels/FindChannelService";

/**
 * The delay for debounce.
 *
 */
const DEBOUNCE_DELAY = 500;

const CHANNELS_PER_FETCH = 10;

/**
 * The reducer function for the find channels form.
 *
 * @param state
 * @param action
 *
 * @returns State
 */
export function reduce(state: State, action: Action): State {
    switch (state.tag) {
        case "navigating":
            switch (action.type) {
                case "search":
                    return { tag: "searching", searchBar: state.searchBar, channels: state.channels }
                case "error":
                    return { tag: "error", error: action.error, channels: [] }
                case "fetchMore":
                    return { tag: "fetchingMore", searchBar: state.searchBar, channels: state.channels }
                case "join":
                    return { tag: "joining", channels: state.channels , searchBar: state.searchBar }
                default:
                    return state
            }
        case "searching":
            switch (action.type) {
                case "success":
                    return { tag: "navigating", searchBar: state.searchBar, channels: action.channels }
                case "error":
                    return { tag: "error", error: action.error, channels: state.channels }
                default:
                    return state
            }
        case "fetchingMore":
            switch (action.type) {
                case "success":
                    return { tag: "navigating", searchBar: state.searchBar, channels: [...state.channels, ...action.channels] }
                case "error":
                    return { tag: "error", error: action.error, channels: state.channels }
                default:
                    return state
            }
        case "error":
            switch (action.type) {
                case "closeError": 
                    return { tag: "navigating", searchBar: "", channels: state.channels } 
                default:
                    return state
            }
        case "joining":
            switch (action.type) {
                case "joined":
                    return { tag: "redirect" }
                case "error":
                    return { tag: "error", error: action.error, channels: state.channels }
                default:
                    return state
            }
        case "redirect":
            throw Error("Already in final State 'redirect' and should not reduce to any other State.")
        default:
            throw Error("Invalid state")
    }
}

export type UseFindChannelsHandler = {
    /**
     * The function to call when the search bar changes.
     * @param searchBar
     * @returns void
     */
    onSearchChange: (searchBar: string) => void

    /**
     * The function to call when the user wants to fetch more channels.
     *
     * @returns void
     */
    onFetch: () => void

    /**
     * The function to call when the user wants to join a channel.
     *
     * @param channelId The id of the channel to join.
     * @returns void
     */
    onJoin: (channelId: string) => void

    /**
     * The function to call when the user wants to close the error message.
     *
     * @returns void
     */
    onErrorClose: () => void
}

const initialState: State = { tag: "navigating", searchBar: "", channels: [] }

export function useFindChannels(
    {getChannelsByPartialName, joinChannel, getPublicChannels}: FindChannelsService = makeDefaultFindChannelService()
) {
    const [state, dispatch] = useReducer(reduce, initialState)
    useEffect(() => {
        if (state.tag != "navigating") return
        const timeout = setTimeout(() => {
            dispatch({ type: "search" })
            onFetch()
        }, DEBOUNCE_DELAY)
        return () => clearTimeout(timeout)
    }, [state]
    )

    const onSearchChange = () => {
        if (state.tag != "navigating") return
        dispatch({ type: "search" })
    }

    const onFetchMore = () => {
        if (state.tag != "navigating") return
        dispatch({ type: "fetchMore" })
        getPublicChannels(
            state.channels.length,
            CHANNELS_PER_FETCH,
            response => response.json().then( (channels) =>
                dispatch({ type: "success", channels: channelsToPublicChannels(channels) })),
            error => dispatch({ type: "error", error: error.message })
        )
    }

    const onFetch = () => {
        if (state.tag != "navigating") return
        dispatch({ type: "search" })
        getChannelsByPartialName(
            state.searchBar,
            response => response.json().then( (channels) =>
                dispatch({ type: "success", channels: channelsToPublicChannels(channels) })),
            error => dispatch({ type: "error", error: error.message })
        )
    }

    const onErrorClose = () => {
        if (state.tag != "error") return
        dispatch({ type: "closeError" })
    }

    const onJoin = (channelId: number) => {
        if (state.tag != "navigating") return
        dispatch({ type: "join", channelId: channelId })
        joinChannel(
            channelId,
            response => response.json().then(() => dispatch({ type: "joined" })),
            error => dispatch({ type: "error", error: error.message })
        )
    }

    return [ state, {onSearchChange} ]
}





















