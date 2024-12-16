import {FindChannelState} from "../states/FindChannelsState";
import {FindChannelAction} from "../states/FindChannelsAction";

/**
 * The reducer function for the find channels form.
 *
 * @param state
 * @param action
 */
export default function (state: FindChannelState, action: FindChannelAction) : FindChannelState {
    switch (state.tag) {
        case "idle":
            switch (action.type) {
                case "init":
                    return { tag: "loading", at: "head" }
                case "error":
                    return { tag: "error", error: action.error, }
                default:
                    throw new Error("Invalid action: " + action.type + " in state: " + state.tag)
            }
        case "loading":
            switch (action.type) {
                case "success":
                    return { tag: "scrolling" }
                case "error":
                    return { tag: "error", error: action.error, }
                default:
                    throw new Error("Invalid action: " + action.type + " in state: " + state.tag)
            }
        case "scrolling":
            switch (action.type) {
                case "loadMore":
                    return { tag: "loading", at: action.at }
                case "error":
                    return { tag: "error", error: action.error, }
                case "join":
                    return { tag: "joining", channelId: action.channelId }
                default:
                    throw new Error("Invalid action: " + action.type + " in state: " + state.tag)
            }
        case "error":
            switch (action.type) {
                case "closeError":
                    return { tag: "idle" }
                default:
                    throw new Error("Invalid action: " + action.type + " in state: " + state.tag)
            }
        case "joining":
            switch (action.type) {
                case "joinSuccess":
                    return { tag: "redirect", channelId: state.channelId }
                case "error":
                    return { tag: "error", error: action.error, }
                default:
                    throw new Error("Invalid action: " + action.type + " in state: " + state.tag)
            }
        case "redirect":
            throw new Error("Final state reached")
    }
}