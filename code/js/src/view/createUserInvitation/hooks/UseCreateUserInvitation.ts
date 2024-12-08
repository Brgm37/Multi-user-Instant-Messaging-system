import {CreateUserInvitationState} from "./states/CreateUserInvitationState";
import {CreateUserInvitationAction} from "./states/CreateUserInvitationAction";
import {UseCreateUserInvitationHandler} from "./handler/UseCreateUserInvitationHandler";
import {useContext, useReducer} from "react";
import {
    CreateUserInvitationServiceContext
} from "../../../service/CreateUserInvitation/createUserInvitationServiceContext";
import {isFailure} from "../../../model/Either";

/**
 * Reducer function for the CreateUserInvitation component.
 *
 * @param state The current state of the component.
 * @param action The action to perform.
 * @returns The new state of the component.
 */
export function reduce(state: CreateUserInvitationState, action: CreateUserInvitationAction): CreateUserInvitationState {
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

const initialState: CreateUserInvitationState = { tag: "editingInvitationCode" }

export function useCreateUserInvitation(): [CreateUserInvitationState, UseCreateUserInvitationHandler] {
    const { createUserInvitation } = useContext(CreateUserInvitationServiceContext)
    const [state, dispatch] = useReducer(reduce, initialState)

    const onCreate = (expirationDate: string) => {
        if (state.tag !== "editingInvitationCode") return
        dispatch({ type: "create", expirationDate })
        createUserInvitation(expirationDate).then(r => {
                if (isFailure(r)) dispatch({type: "error", message: r.value})
                else{
                    dispatch({ type: "success", invitationCode: r.value.invitationCode })
                }
            }
        )
    }

    const onClose = () => {
        if (state.tag === "error") return
        dispatch({ type: "close" })
    }

    const onErrorClose = () => {
        if (state.tag !== "error") return
        dispatch({ type: "close" })
    }

    return [state, { onCreate, onClose, onErrorClose }]
}