import {JoinChannelActions} from "../states/JoinChannelActions";
import {JoinChannelState} from "../states/JoinChannelState";

/**
 * The reducer for the JoinChannel component.
 *
 * @param state The current state.
 * @param action The action to perform.
 * @returns The new state.
 */
export default function (state: JoinChannelState, action: JoinChannelActions) : JoinChannelState {
    switch (state.tag) {
        case "UseJoin":
            switch (action.type) {
                case "success":
                    return { tag: "UseJoinSuccess", id: action.id }
                case "error":
                    return { tag: "UseJoinError", message: action.message }
                case "close":
                    return { tag: "UseJoinClose" }
                default:
                    throw new Error("Invalid action")
            }
        case "UseJoinError":
            switch (action.type) {
                case "close":
                    return { tag: "UseJoinClose" }
                default:
                    throw new Error("Invalid action")
            }
        case "UseJoinClose":
            throw new Error("Final state reached")
        case "UseJoinSuccess":
            throw new Error("Final state reached")
    }
}