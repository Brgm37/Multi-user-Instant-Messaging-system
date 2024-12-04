import {CreateChannelsState, makeInitialState} from "./states/createChannelsState";
import {CreateChannelsAction} from "./states/createChannelsAction";
import {useContext, useEffect, useReducer} from "react";
import {CreateChannelsServiceContext} from "../../../service/createChannels/createChannelsServiceContext";
import {isFailure} from "../../../model/Either";
import {UseCreateChannelFormHandler} from "./handler/UseCreateChannelFormHandler";

/**
 * The delay for debounce.
 */
const DEBOUNCE_DELAY = 500;

const ERROR_MESSAGE = "Channel name already exists"

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
    const service = useContext(CreateChannelsServiceContext)
    useEffect(() => {
        if (state.tag !== "editing") return
        const timeout = setTimeout(() => {
            const fetchChannel=
                state.input.name !== "" ?
                service.findChannelByName(state.input.name) :
                null
            fetchChannel
                .then(response => {
                    if (isFailure(response)) dispatch({type: "error", error: response.value})
                    else dispatch({type: "validation-result", response: response.value.toString()})
                })
        }, DEBOUNCE_DELAY)
        return () => clearTimeout(timeout)
    }, [state.tag]);

    const handler: UseCreateChannelFormHandler = {
        onNameChange(name: string) {
            service
                .findChannelByName(name)
                .then(response => {
                    if (response.tag === "success"){
                        dispatch({type: "error", error: ERROR_MESSAGE})
                    }else null
                })
            dispatch({type: "edit", input: "name", inputValue: name})
        }
        
    }
    return [state, handler]
}














