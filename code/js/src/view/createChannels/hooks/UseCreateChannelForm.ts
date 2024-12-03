import {CreateChannelsState, makeInitialState} from "./states/createChannelsState";
import {CreateChannelsAction} from "./states/createChannelsAction";
import {useReducer} from "react";

function reduce(state: CreateChannelsState, action: CreateChannelsAction): CreateChannelsState {
    switch (state.tag) {
        case "editing":
            switch (action.type) {
                case "edit": {
                    const input = {...state.input, [action.input]: action.inputValue}
                    return {...state, input}
                }
                case "submit": {
                    return {tag: "submitting", input: state.input}
                }
                case "validation-result": {
                    const {response} = action
                    const error = response === true ? "" : response
                    const isValid = response === true
                    const input = {...state.input, isValid}
                    return {...state, error, input}
                }
                default:
                    throw Error("Invalid action")
            }
        case "submitting":
            switch (action.type) {
                case "success":
                    return {tag: "redirecting"}
                case "error":
                    return {tag: "error", message: action.error, input: state.input}
                default:
                    throw Error("Invalid action")
            }
        case "error":
            switch (action.type) {
                case "edit":
                    const input = {...state.input, [action.input]: action.inputValue}
                    return {tag: "editing", input, error: ""}
                default:
                    throw Error("Invalid action")
            }
        case "redirecting":
            throw Error("Already in final State 'redirecting' and should not reduce to any other State.")
        default:
            throw Error("Invalid state")
    }
}

export function useCreateChannelForm(): [CreateChannelsState,CreateChannelsAction] {
    const [state, dispatch] = useReducer(reduce, makeInitialState())
    return [state, dispatch]
}














