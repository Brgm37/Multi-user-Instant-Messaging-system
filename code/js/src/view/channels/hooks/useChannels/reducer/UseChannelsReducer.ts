import {ChannelsState} from "../state/ChannelsState";
import {ChannelsAction} from "../state/ChannelsAction";

/**
 * UseChannelsReducer is a function that takes the current state and an action and returns the new state.
 *
 * @param state
 * @param action
 */
export default function (state: ChannelsState, action: ChannelsAction): ChannelsState {
    switch (state.tag) {
        case "idle":
            switch (action.tag) {
                case "init":
                    return {tag: "loading", at: "both"}
                default:
                    throw Error(`Action ${action.tag} is not allowed in state ${state.tag}`)
            }
        case "loading":
            switch (action.tag) {
                case "loadSuccess":
                    return {tag: "scrolling"}
                case "loadError":
                    return {tag: "error", message: action.message, previous: state}
                default:
                    throw Error(`Action ${action.tag} is not allowed in state ${state.tag}`)
            }
        case "scrolling":
            switch (action.tag) {
                case "loadMore":
                    return {tag: "loading", at: action.at}
                default:
                    throw Error(`Action ${action.tag} is not allowed in state ${state.tag}`)
            }
        case "error":
            switch (action.tag) {
                case "goBack":
                    return state.previous
                default:
                    throw Error(`Action ${action.tag} is not allowed in state ${state.tag}`)
            }
    }
}