import {CreateChannelInvitationAction} from "../states/CreateChannelInvitationAction";
import {CreateChannelInvitationState} from "../states/CreateChannelInvitationState";

/**
 * The reducer function.
 *
 * @param state
 * @param action
 */
export default function (state: CreateChannelInvitationState, action: CreateChannelInvitationAction): CreateChannelInvitationState {
    switch (state.tag) {
        case "editingInvitationToken":
            switch (action.type) {
                case "create":
                    return { tag: "creating" }
                case "close":
                    return { tag: "closing", cId: action.cId }
                case "error":
                    return { tag: "error", error: action.error }
                default:
                    return state
            }
        case "showingInvitationToken":
            switch (action.type) {
                case "close":
                    return { tag: "closing", cId: action.cId }
                default:
                    return state
            }
        case "creating":
            switch (action.type) {
                case "success":
                    return { tag: "showingInvitationToken", invitationToken: action.invitationToken }
                case "error":
                    return { tag: "error", error: action.error }
                default:
                    return state
            }
        case "error":
            switch (action.type) {
                case "close":
                    return { tag: "closing", cId: action.cId }
                default:
                    return state
            }
        case "closing":
            return state
        default:
            return state
    }
}