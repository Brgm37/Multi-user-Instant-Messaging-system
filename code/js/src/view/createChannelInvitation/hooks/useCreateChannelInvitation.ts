import {CreateChannelInvitationState} from "./states/CreateChannelInvitationState";
import {UseCreateChannelInvitationHandler} from "./handler/UseCreateChannelInvitationHandler";
import {useContext, useReducer} from "react";
import {isFailure} from "../../../model/Either";
import {
    CreateChannelInvitationServiceContext
} from "../../../service/createChannelInvitation/CreateChannelInvitationServiceContext";
import {useParams} from "react-router-dom";
import reduce from "./reducer/CreateChannelInvitationReducer";

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