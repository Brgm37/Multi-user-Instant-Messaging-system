import {ChannelInput, CreateChannelsState, makeInitialState} from "./states/createChannelsState";
import {CreateChannelsAction} from "./states/createChannelsAction";
import {useContext, useEffect, useReducer} from "react";
import {CreateChannelsServiceContext} from "../../../service/createChannels/createChannelsServiceContext";
import {UseCreateChannelHandler} from "./handler/UseCreateChannelHandler";

/**
 * The delay for debounce.
 */
const DEBOUNCE_DELAY = 500;

/**
 * The error message.
 */
const ERROR_MESSAGE = "An error occurred while creating the channel. Please try again later.";

/**
 * Reduces the state.
 *
 * @param state
 * @param action
 */
function reduce(state: CreateChannelsState, action: CreateChannelsAction): CreateChannelsState {
    switch (state.tag) {
        case "editing":
            switch (action.type) {
                case "editName": {
                    const input = {
                        name: action.inputValue,
                        visibility: state.input.visibility,
                        access: state.input.access,
                        description: state.input.description,
                        icon: state.input.icon
                    }
                    return { tag: "editing", input: input };
                }
                case "editDescription": {
                    const input = { ...state.input, description: action.inputValue };
                    return { tag: "editing", input: input };
                }
                case "validated": {
                    const input = {
                        name: state.input.name,
                        visibility: state.input.visibility,
                        access: state.input.access,
                        description: state.input.description,
                        icon: state.input.icon
                    }
                    return { tag: "editing", input: input };
                }
                case "validation": {
                    if (action.name === state.input.name) {
                        const input = {...state.input, name: action.name};
                        return {tag: "validating", input: input};
                    }
                    return state;
                }
                case "submit": {
                    return { tag: "submitting", input: state.input };
                }
                default:
                    throw Error("Invalid action" + action.type);
            }
        case "submitting":
            switch (action.type) {
                case "success":
                    return { tag: "redirecting", input: state.input };
                case "error":
                    return { tag: "error", message: action.message, input: state.input };
                default:
                    throw Error("Invalid action");
            }
        case "error":
            throw Error("Already in final State 'redirecting' and should not reduce to any other State.");
        case "validating":
            switch (action.type) {
                case "editName": {
                    const input = {
                        name: action.inputValue,
                        visibility: state.input.visibility,
                        access: state.input.access,
                        description: state.input.description,
                        icon: state.input.icon
                    }
                    return { tag: "editing", input: input };
                }
                case "editDescription": {
                    const input = { ...state.input, description: action.inputValue };
                    return { tag: "editing", input: input };
                }
                case "validated": {
                    const input = { ...state.input, isValid: action.isValidInput };
                    return { tag: "validating", input: input };
                }
                case "submit": {
                    return { tag: "submitting", input: state.input };
                }
                default:
                    throw Error("Invalid action");
            }
        case "redirecting":
            throw Error("Already in final State 'redirecting' and should not reduce to any other State.");
        default:
            throw Error("Invalid state");
    }
}

/**
 * The useCreateChannel hook.
 */
export function useCreateChannel(): [CreateChannelsState,UseCreateChannelHandler] {
    const [state, dispatch] = useReducer(reduce, makeInitialState())
    const service = useContext(CreateChannelsServiceContext)

    useEffect(() => {
        const timeout = setTimeout(() => {
            if (state.tag !== "editing") return;
            if (state.input.name === "") return;
            dispatch({ type: "validation", name: state.input.name });
            service.findChannelByName(state.input.name).then(response => {
                if (response.tag === "success") {
                    dispatch({ type: "validated", isValidInput: false });
                }else{
                    dispatch({ type: "validated", isValidInput: true });
                }
            })

        }, DEBOUNCE_DELAY);
        return () => clearTimeout(timeout);
    }, [state.input.name]);

    const handler: UseCreateChannelHandler = {
        onNameChange(name: string) {
            if (state.tag !== "editing" && state.tag !== "validating") return;
            dispatch({ type: "editName", inputValue: name })
        },

        onDescriptionChange(description: string) {
            if (state.tag !== "editing" && state.tag !== "validating") return;
            dispatch({ type: "editDescription", inputValue: description });
        },
        onSubmit(channel: ChannelInput) {
            if (state.tag !== "validating" && state.tag !== "editing") return;
            if (!state.input.isValid) return;
            service.createChannel(channel.name, channel.visibility, channel.access, channel.description, channel.icon)
                .then(response => {
                    if (response.tag === "success") dispatch({ type: "success", input: state.input });
                    else dispatch({ type: "error", message: ERROR_MESSAGE});
                });
            dispatch({ type: "submit" });
        }
    };
    return [state, handler]
}














