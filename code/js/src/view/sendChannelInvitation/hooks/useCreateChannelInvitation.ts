import {SendChannelInvitationState} from "./states/SendChannelInvitationState";
import {SendChannelInvitationAction} from "./states/SendChannelInvitationAction";
import {UseSendChannelInvitationHandler} from "./handler/UseSendChannelInvitationHandler";
import {useContext, useReducer} from "react";


export function reduce(state: SendChannelInvitationState, action: SendChannelInvitationAction): SendChannelInvitationState {
    switch (state.tag) {
        case "editingInvitationToken":
            switch (action.type) {
                case "create":
                    return { tag: "creating" }
                case "close":
                    return { tag: "closing" }
                case "error":
                    return { tag: "error", error: action.error }
                default:
                    return state
            }
        case "showingInvitationToken":
            switch (action.type) {
                case "close":
                    return { tag: "closing" }
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
                    return { tag: "closing" }
                default:
                    return state
            }
        case "closing":
            return state
        default:
            return state
    }
}

const initialState: SendChannelInvitationState = { tag: "editingInvitationToken", inputs: { maxUses: 1, accessControl: "READ_WRITE"} }

export function useSendChannelInvitation(): [SendChannelInvitationState, UseSendChannelInvitationHandler] {
    // const context = useContext(SendChannelInvitationContext)
    const [state, dispatch] = useReducer(reduce, initialState)

    const onCreate = (expirationDate: string, maxUses: number, accessControl: "READ_ONLY" | "READ_WRITE") => {
        if (state.tag !== "editingInvitationToken") return
        dispatch({ type: "create", expirationDate, maxUses, accessControl })
    }

    const onClose = () => {
        dispatch({ type: "close" })
    }

    const onErrorClose = () => {
        dispatch({ type: "close" })
    }

    return [state, { onCreate, onClose, onErrorClose }]
}