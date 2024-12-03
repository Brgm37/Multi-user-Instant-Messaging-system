import {CreateChannelInvitationState} from "./states/CreateChannelInvitationState";
import {CreateChannelInvitationAction} from "./states/CreateChannelInvitationAction";
import {UseCreateChannelInvitationHandler} from "./handler/UseCreateChannelInvitationHandler";
import {useContext, useReducer} from "react";
import {
    CreateChannelInvitationMockServiceContext
} from "../../../service/createChannelInvitation/mock/CreateChannelInvitationMockServiceContext";
import {isFailure} from "../../../model/Either";


export function reduce(state: CreateChannelInvitationState, action: CreateChannelInvitationAction): CreateChannelInvitationState {
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

const initialState: CreateChannelInvitationState = { tag: "editingInvitationToken", inputs: { maxUses: 1, accessControl: "READ_WRITE"} }

export function useCreateChannelInvitation(): [CreateChannelInvitationState, UseCreateChannelInvitationHandler] {
    const { createChannelInvitation } = useContext(CreateChannelInvitationMockServiceContext)
    const [state, dispatch] = useReducer(reduce, initialState)

    const onCreate = (expirationDate: string, maxUses: string, accessControl: "READ_ONLY" | "READ_WRITE") => {
        if (state.tag !== "editingInvitationToken") return
        let maxUsesInt = parseInt(maxUses)
        dispatch({ type: "create", expirationDate, maxUses: maxUsesInt, accessControl })
        createChannelInvitation(expirationDate, maxUsesInt, accessControl).then(r => {
                if (isFailure(r)) dispatch({type: "error", error: r.value})
                else dispatch({ type: "success", invitationToken: r.value.invitationCode })
            }
        )
    }

    const onClose = () => {
        dispatch({ type: "close" })
    }

    const onErrorClose = () => {
        dispatch({ type: "close" })
    }

    return [state, { onCreate, onClose, onErrorClose }]
}