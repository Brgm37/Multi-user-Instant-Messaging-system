import {CreateUserInvitationState} from "./states/CreateUserInvitationState";
import reduce from "./reducer/CreateUserInvitationReducer";
import {UseCreateUserInvitationHandler} from "./handler/UseCreateUserInvitationHandler";
import {useContext, useReducer} from "react";
import {
    CreateUserInvitationServiceContext
} from "../../../service/CreateUserInvitation/createUserInvitationServiceContext";
import {isFailure} from "../../../model/Either";

/**
 * The initial state of the CreateUserInvitation component.
 */
const initialState: CreateUserInvitationState = { tag: "editingInvitationCode" }

/**
 * The useCreateUserInvitation hook.
 */
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