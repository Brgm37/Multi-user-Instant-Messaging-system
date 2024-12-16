import {ChannelInput, CreateChannelsState, makeInitialState} from "./states/createChannelsState";
import reduce from "./reducer/CreateChannelReducer";
import {useContext, useEffect, useReducer} from "react";
import {CreateChannelsServiceContext} from "../../../service/createChannels/CreateChannelsServiceContext";
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
 * The useCreateChannel hook.
 */
export function useCreateChannel(): [CreateChannelsState, UseCreateChannelHandler] {
    const [state, dispatch] = useReducer(reduce, makeInitialState())
    const service = useContext(CreateChannelsServiceContext)

    useEffect(() => {
        const timeout = setTimeout(() => {
            if (state.tag !== "editing") return;
            if (state.input.name === "") return;
            dispatch({type: "validation", name: state.input.name});
            service.findChannelByName(state.input.name).then(response => {
                if (response.tag === "success") {
                    dispatch({type: "validated", isValidInput: false});
                } else {
                    dispatch({type: "validated", isValidInput: true});
                }
            })

        }, DEBOUNCE_DELAY);
        return () => clearTimeout(timeout);
    }, [state.input.name]);

    const handler: UseCreateChannelHandler = {
        onNameChange(name: string) {
            if (state.tag !== "editing" && state.tag !== "validating") return;
            dispatch({type: "editName", inputValue: name})
        },

        onDescriptionChange(description: string) {
            if (state.tag !== "editing" && state.tag !== "validating") return;
            dispatch({type: "editDescription", inputValue: description});
        },
        onSubmit(channel: ChannelInput) {
            if (state.tag !== "validating" && state.tag !== "editing") return;
            if (!state.input.isValid) return;
            service.createChannel(channel.name, channel.visibility, channel.access, channel.description, channel.icon)
                .then(response => {
                    if (response.tag === "success") dispatch({type: "success", input: state.input});
                    else dispatch({type: "error", message: ERROR_MESSAGE});
                });
            dispatch({type: "submit"});
        }
    };
    return [state, handler]
}














