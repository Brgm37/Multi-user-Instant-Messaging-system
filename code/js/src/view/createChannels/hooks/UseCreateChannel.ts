import {CreateChannelsState, makeInitialState} from "./states/createChannelsState";
import {CreateChannelsAction} from "./states/createChannelsAction";
import {useContext, useEffect, useReducer} from "react";
import {CreateChannelsServiceContext} from "../../../service/createChannels/createChannelsServiceContext";
import {isFailure} from "../../../model/Either";
import {UseCreateChannelHandler} from "./handler/UseCreateChannelHandler";

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
                default:
                    throw Error("Invalid action")
            }
        case "submitting":
            switch (action.type) {
                case "success":
                    return {tag: "redirecting", input: state.input}
                case "error":
                    return {tag: "error", message: action.message, input: state.input}
                default:
                    throw Error("Invalid action")
            }
        case "error":
            switch (action.type) {
                case "edit":
                    const input = {...state.input, [action.input]: action.inputValue }
                    return {tag: "editing", input}
                default:
                    throw Error("Invalid action")
            }
        case "redirecting":
            throw Error("Already in final State 'redirecting' and should not reduce to any other State.")
        default:
            throw Error("Invalid state")
    }
}

export function useCreateChannel(): [CreateChannelsState,UseCreateChannelHandler] {
    const [state, dispatch] = useReducer(reduce, makeInitialState())
    const service = useContext(CreateChannelsServiceContext)

    useEffect(() => {
         if (state.tag !== "editing") return
         const timeout = setTimeout(() => {
             const fetchChannel=
                 state.input.name !== "" ?
                 service.findChannelByName(state.input.name) :
                 null
             if(fetchChannel){
                 fetchChannel
                     .then(response => {
                         if (isFailure(response)) dispatch({type: "success", input: state.input})
                         else dispatch({type: "error", message: ERROR_MESSAGE})
                     })
             }
         }, DEBOUNCE_DELAY)
         return () => clearTimeout(timeout)
    }, [state.tag, state.input.name]);

    useEffect(() => {
        if (state.tag !== "submitting") return
        service
            .createChannel(state.input.name, state.input.visibility, state.input.access)
            .then(response => {
                if (isFailure(response)) dispatch({type: "error", message: response.value})
                else dispatch({type: "success", input: state.input})
            })
    }, [state.tag]);

    const handler: UseCreateChannelHandler = {
        onNameChange(name: string) {
            if (state.tag !== "editing") return
            service
                .findChannelByName(name)
                .then(response => {
                    if (response.tag === "success"){
                        dispatch({type: "error", message: ERROR_MESSAGE})
                        state.input.isValid = false
                    }
                })
            dispatch({type: "edit", input: "name", inputValue: name})
        },
        onVisibilityChange(visibility: string) {
            if (state.tag !== "editing") return
            dispatch({type: "edit", input: "visibility", inputValue: visibility})
        },
        onAccessChange(access: string) {
            if (state.tag !== "editing") return
            dispatch({type: "edit", input: "access", inputValue: access})
        },
        onSubmit() {
            if (state.tag !== "editing") return
            if (!state.input.isValid) return
            service
                .createChannel(state.input.name, state.input.visibility, state.input.access)
                .then(response => {
                    if (response.tag === "success") dispatch({type: "success", input: state.input})
                    else dispatch({type: "error", message: response.value})
                })
            dispatch({type: "submit"})
        }

        
    }
    return [state, handler]
}














