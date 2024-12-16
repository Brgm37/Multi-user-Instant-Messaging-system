import {CreateUserInvitationAction} from "../states/CreateUserInvitationAction";
import {CreateUserInvitationState} from "../states/CreateUserInvitationState";

/**
 * Reducer function for the createUserInvitation component.
 *
 * @param state The current state of the component.
 * @param action The action to perform.
 * @returns The new state of the component.
 */
export default function (state: CreateUserInvitationState, action: CreateUserInvitationAction) : CreateUserInvitationState {
    switch (state.tag) {
        case "editingInvitationCode":
            switch (action.type) {
                case "create":
                    return { tag: "creating" }
                case "close":
                    return { tag: "closing" }
                case "error":
                    return { tag: "error", message: action.message }
                default:
                    throw Error("Invalid action")
            }
        case "showingInvitationCode":
            switch (action.type) {
                case "close":
                    return { tag: "closing" }
                default:
                    throw Error("Invalid action")
            }
        case "creating":
            switch (action.type) {
                case "success":
                    return { tag: "showingInvitationCode", invitationCode: action.invitationCode }
                case "error":
                    return { tag: "error", message: action.message }
                default:
                    throw Error("Invalid action")
            }
        case "error":
            switch (action.type) {
                case "close":
                    return { tag: "closing" }
                default:
                    throw Error("Invalid action")
            }
        case "closing":
            throw Error("Already in final State 'redirecting' and should not reduce to any other State.")
        default:
            throw Error("Invalid state")
    }

}