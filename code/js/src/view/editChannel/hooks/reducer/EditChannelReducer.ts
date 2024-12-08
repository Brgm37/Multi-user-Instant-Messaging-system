import {EditChannelState} from "../states/EditChannelState";
import {EditChannelAction} from "../states/EditChannelAction";

/**
 * The reducer function.
 * @param state
 * @param action
 */
export default function (state: EditChannelState, action: EditChannelAction): EditChannelState {
    switch (state.tag) {
        case "idle":
            switch (action.type) {
                case "init":
                    return {tag: "loading"}
                default:
                    throw new Error("Invalid action: " + action.type + " in state: " + state.tag)
            }
        case "loading":
            switch (action.type) {
                case "loadSuccess":
                    return {tag: "editing", channel: action.channel}
                case "loadError":
                    return {tag: "error", message: action.error}
                default:
                    throw new Error("Invalid action: " + action.type + " in state: " + state.tag)
            }
        case "editing":
            switch (action.type) {
                case "submit":
                    return {tag: "submitting"}
                default:
                    throw new Error("Invalid action: " + action.type + " in state: " + state.tag)
            }
        case "submitting":
            switch (action.type) {
                case "editSuccess":
                    return {tag: "redirect", cId: action.cId}
                case "editError":
                    return {tag: "error", message: action.error}
                default:
                    throw new Error("Invalid action: " + action.type + " in state: " + state.tag)
            }
        case "error":
            switch (action.type) {
                case "closeError":
                    return {tag: "idle"}
                default:
                    throw new Error("Invalid action: " + action.type + " in state: " + state.tag)
            }
        case "redirect":
            throw new Error("Final state reached")
    }
}