import {CreateChannelInvitationState} from "./states/CreateChannelInvitationState";
import {CreateChannelInvitationAction} from "./states/CreateChannelInvitationAction";
import {UseCreateChannelInvitationHandler} from "./handler/UseCreateChannelInvitationHandler";
import {useContext, useReducer} from "react";
import {
    CreateChannelInvitationMockServiceContext
} from "../../../service/createChannelInvitation/mock/CreateChannelInvitationMockServiceContext";
import {isFailure} from "../../../model/Either";
import {
    CreateChannelInvitationServiceContext
} from "../../../service/createChannelInvitation/CreateChannelInvitationServiceContext";
import {useParams} from "react-router-dom";

/**
 * The reducer function.
 *
 * @param state
 * @param action
 */
export function reduce(state: CreateChannelInvitationState, action: CreateChannelInvitationAction): CreateChannelInvitationState {
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

/**
 * The initial state.
 */
const initialState: CreateChannelInvitationState = { tag: "editingInvitationToken", inputs: { maxUses: 1, accessControl: "READ_WRITE"} }

/**
 * The useCreateChannelInvitation hook.
 */
export function useCreateChannelInvitation(): [CreateChannelInvitationState, UseCreateChannelInvitationHandler] {
    const { createChannelInvitation } = useContext(CreateChannelInvitationServiceContext)
    const [state, dispatch] = useReducer(reduce, initialState)
    const { id } = useParams<{ id: string }>();

    const onCreate = (expirationDate: string, maxUses: string, accessControl: "READ_ONLY" | "READ_WRITE") => {
        if (state.tag !== "editingInvitationToken") return
        const maxUsesInt = Number(maxUses)
        dispatch({ type: "create", expirationDate, maxUses: maxUsesInt, accessControl })
        createChannelInvitation(expirationDate, maxUsesInt, accessControl, id).then(r => {
                if (isFailure(r)) dispatch({type: "error", error: r.value})
                else dispatch({ type: "success", invitationToken: r.value.invitationCode })
            }
        )
    }

    const onClose = () => {
        dispatch({ type: "close", cId: id })
    }

    const onErrorClose = () => {
        dispatch({ type: "close", cId: id })
    }

    return [state, { onCreate, onClose, onErrorClose }]
}