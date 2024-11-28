import {Action} from "./states/FindChannelsAction";
import {State} from "./states/FindChannelsState";

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