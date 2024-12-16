import {CreateChannelsAction} from "../states/createChannelsAction";
import {CreateChannelsState} from "../states/createChannelsState";

/**
 * Reduces the state.
 *
 * @param state
 * @param action
 */
export default function (state: CreateChannelsState, action: CreateChannelsAction) : CreateChannelsState {
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