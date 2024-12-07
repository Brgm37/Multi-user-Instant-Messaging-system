import {FindChannelState} from "../states/FindChannelsState";
import {FindChannelAction} from "../states/FindChannelsAction";

/**
 * The reducer function for the find channels form.
 *
 * @param state
 * @param action
 *
 * @returns FindChannelState
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
                    throw new Error("Invalid action: " + action.type)
            }
        case "loading":
            switch (action.type) {
                case "success":
                    return { tag: "scrolling" }
                case "error":
                    return { tag: "error", error: action.error, }
                default:
                    throw new Error("Invalid action: " + action.type)
            }
        case "scrolling":
            switch (action.type) {
                case "loadMore":
                    return { tag: "loading", at: action.at }
                case "error":
                    return { tag: "error", error: action.error, }
                default:
                    throw new Error("Invalid action: " + action.type)
            }
        case "error":
            switch (action.type) {
                case "closeError":
                    return { tag: "idle" }
                default:
                    throw new Error("Invalid action: " + action.type)
            }
        case "joining":
            switch (action.type) {
                case "joinSuccess":
                    return { tag: "redirect", channelId: state.channelId }
                case "error":
                    return { tag: "error", error: action.error, }
                default:
                    throw new Error("Invalid action: " + action.type)
            }
        case "redirect":
            throw new Error("Final state reached")
    }
}