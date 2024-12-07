import {ChannelState} from "../states/ChannelState";
import {ChannelAction} from "../states/ChannelAction";

/**
 * Reducer is a function that takes a state and an action and returns a new state.
 *
 * @param state
 * @param action
 */
export default function (state: ChannelState, action: ChannelAction): ChannelState {
    switch (state.tag) {
        case "idle":
            switch (action.tag) {
                case "init":
                    return {tag: "loading", at: "both"}
                default:
                    throw new Error(`Invalid action ${action.tag} for state ${state.tag}`)
            }
        case "loading":
            switch (action.tag) {
                case "loadSuccess":
                    return {tag: "messages"}
                case "loadError":
                    return {tag: "error", message: action.error, previous: state}
                case "sendSuccess":
                    return {tag: "messages"}
                case "sendError":
                    return {tag: "error", message: action.error, previous: state}
                case "reset":
                    return {tag: "idle"}
                default:
                    throw new Error(`Invalid action ${action.tag} for state ${state.tag}`)
            }
        case "error":
            switch (action.tag) {
                case "go-back":
                    return state.previous
                case "reset":
                    return {tag: "idle"}
                default:
                    throw new Error(`Invalid action ${action.tag} for state ${state.tag}`)
            }
        case "messages":
            switch (action.tag) {
                case "loadMore":
                    return {tag: "loading", at: action.at}
                case "sendMessage":
                    return {tag: "loading", at: "sending"}
                case "receiving-sse":
                    return {tag: "loading", at: "receiving"}
                case "reload":
                    return {tag: "idle"}
                case "reset":
                    return {tag: "idle"}
                default:
                    throw new Error(`Invalid action ${action.tag} for state ${state.tag}`)
            }
    }
}