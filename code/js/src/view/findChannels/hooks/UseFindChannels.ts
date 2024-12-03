import {Action} from "./states/FindChannelsAction";
import {FindChannelState} from "./states/FindChannelsState";
import {useContext, useEffect, useReducer} from "react";
import {UseFindChannelsHandler} from "./handler/UseFindChannelsHandler";
import {FindChannelsMockServiceContext} from "../../../service/findChannels/mock/FindChannelsMockServiceContext";
import {isFailure} from "../../../model/Either";
import envConfig from "../../../../envConfig.json"
import {channelsToPublicChannels} from "../../../model/PublicChannel";

/**
 * The delay for debounce.
 */
const DEBOUNCE_DELAY = 500;

/**
 * The number of channels to fetch per request.
 */
const CHANNELS_PER_FETCH = envConfig.public_channels_limit

/**
 * The default offset.
 */
const DEFAULT_OFFSET = envConfig.default_offset

/**
 * The reducer function for the find channels form.
 *
 * @param state
 * @param action
 *
 * @returns FindChannelState
 */
export function reduce(state: FindChannelState, action: Action): FindChannelState {
    switch (state.tag) {
        case "navigating":
            switch (action.type) {
                case "error":
                    return { tag: "error", error: action.error, channels: [], searchBar: state.searchBar }
                case "fetchMore":
                    return { tag: "fetchingMore", searchBar: state.searchBar, channels: state.channels }
                case "join":
                    return { tag: "joining", channels: state.channels , searchBar: state.searchBar, channelId: action.channelId }
                case "edit":
                    return { tag: "editing", searchBar: action.inputValue, channels: [] }
                default:
                    return state
            }
        case "fetchingMore":
            switch (action.type) {
                case "success":
                    return { tag: "navigating", searchBar: state.searchBar, channels: [...state.channels, ...action.channels] }
                case "error":
                    return { tag: "error", error: action.error, channels: state.channels, searchBar: state.searchBar }
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
                    return { tag: "redirect", channelId: state.channelId, searchBar: state.searchBar }
                case "error":
                    return { tag: "error", error: action.error, channels: state.channels, searchBar: state.searchBar }
                default:
                    return state
            }
        case "editing":
            switch (action.type) {
                case "error":
                    return { tag: "error", error: action.error, channels: [], searchBar: state.searchBar };
                case "fetchMore":
                    return { tag: "fetchingMore", searchBar: state.searchBar, channels: state.channels };
                case "join":
                    return { tag: "joining", channels: state.channels, searchBar: state.searchBar, channelId: action.channelId };
                case "edit":
                    return { tag: "editing", searchBar: action.inputValue, channels: [] };
                case "success":
                    return { tag: "navigating", searchBar: state.searchBar, channels: action.channels };
                default:
                    return state;
            }
        case "redirect":
            throw Error("Already in final State 'redirect' and should not reduce to any other State.")
        default:
            throw Error("Invalid state")
    }
}

const initialState: FindChannelState = { tag: "navigating", searchBar: "", channels: [] }

export function useFindChannels(): [FindChannelState, UseFindChannelsHandler] {
    const { getChannelsByPartialName, getPublicChannels, joinChannel } = useContext(FindChannelsMockServiceContext)
    const [state, dispatch] = useReducer(reduce, initialState)

    useEffect(() => {
        if (state.tag !== "editing") return;
        const timeout = setTimeout(() => {
            const fetchChannels =
                state.searchBar === ""
                    ? getPublicChannels(DEFAULT_OFFSET, CHANNELS_PER_FETCH)
                    : getChannelsByPartialName(state.searchBar);
            fetchChannels
                .then((response) => {
                    if (isFailure(response)) dispatch({ type: "error", error: response.value })
                    else dispatch({ type: "success", channels: channelsToPublicChannels(response.value) })
                })
        }, DEBOUNCE_DELAY);
        return () => clearTimeout(timeout);
    }, [state.searchBar, state.tag]);

    const onSearchChange = (searchBar: string) => {
        if (state.tag !== "navigating" && state.tag !== "editing") return
        dispatch({type: "edit", inputValue: searchBar, inputName: "searchBar"})
    }

    const onFetchMore = () => {
        if (state.tag != "navigating") return
        dispatch({type: "fetchMore"})
        getPublicChannels(state.channels.length, CHANNELS_PER_FETCH)
            .then((response) => {
                if (isFailure(response)) {
                    dispatch({type: "error", error: response.value})
                } else {
                    dispatch({type: "success", channels: channelsToPublicChannels(response.value)})
                }
            })
    }

    const onErrorClose = () => {
        if (state.tag != "error") return
        dispatch({type: "closeError"})
    }

    const onJoin = (channelId: number) => {
        if (state.tag != "navigating") return
        dispatch({type: "join", channelId: channelId})
        joinChannel(channelId)
            .then((response) => {
                if (isFailure(response)) dispatch({ type: "error", error: response.value })
                else dispatch({type: "joined"})
            })
    }

    return [state, {onSearchChange, onErrorClose, onJoin, onFetchMore}]
}





















