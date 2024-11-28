import {Action} from "./states/FindChannelsAction";
import {State} from "./states/FindChannelsState";
import {useEffect, useReducer} from "react";
import {useFetch} from "../../../service/utils/useFetch";
import {urlBuilder} from "../../../service/utils/UrlBuilder";
import {channelsToPublicChannels} from "../model/findChannels/PublicChannel";

/**
 * The URL for the find channels API.
 */
const findChannelsApiUrl = urlBuilder("/channels/search")

/**
 * The delay for debounce.
 *
 */
const DEBOUNCE_DELAY = 500;

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
                    return { tag: "fetchingMore", searchBar: state.searchBar, channels: [...state.channels, ...action.channels] }
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

export function useFindChannels() {
    const [state, dispatch] = useReducer(reduce, initialState)

    useEffect(() => {
        if (state.tag != "navigating") return
        const timeout = setTimeout(() => {
            dispatch({ type: "search", channels: [] })
        }, DEBOUNCE_DELAY)
        return () => clearTimeout(timeout)
    }, [state]
    )

    const onSearchChange = () => {
        if (state.tag != "navigating") return
        useFetch(
            findChannelsApiUrl + '/' + state.searchBar,
            "GET",
            response => response.json().then((channels) =>
                dispatch({ type: "success", channels: channelsToPublicChannels(channels) })),
            response => dispatch({ type: "error", error: response.message }),
            {}
        ).toFetch()
        dispatch({ type: "search", channels: [] })
    }

    const onFetch = () => {
        dispatch({ type: "fetchMore", channels: [] })
    }

    const onErrorClose = () => {
        dispatch({ type: "closeError" })
    }

    const onJoin = (channelId: number) => {
        dispatch({ type: "join", channelId: channelId })
    }

    return [ state, {onSearchChange} ]
}





















