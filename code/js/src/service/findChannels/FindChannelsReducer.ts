import { PublicChannel } from "../../model/findChannels/PublicChannel"

/**
 * @description Type for the state of the PublicChannels form.
 * 
 * @type State
 * @prop searchBar The search bar input.
 * @prop channels The list of public channels.
 */
type State = 
    { tag: 
        "navigating" | 
        "searching" | 
        "fetchingMore", 
        searchBar: string, channels: PublicChannel[]
    } |
    { tag: "error", error: string, channels: PublicChannel[] }

/**
 * The Action type for the PublicChannels form.
 * 
 * @type Action
 * @prop type The type of the action.
 * @prop channels The list of public channels.
 * @prop error The error message.
 * @prop searchBar The search bar input.
*/
type Action = 
    { type: "search", channels: PublicChannel[] } |
    { type: "error", error: string} |
    { type: "success", channels: PublicChannel[] } |
    { type: "fetchMore", channels: PublicChannel[] } |
    { type: "closeError" }

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