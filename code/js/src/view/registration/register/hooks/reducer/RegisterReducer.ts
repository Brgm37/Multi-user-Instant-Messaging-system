import {RegisterState} from "../states/RegisterState";
import {RegisterAction} from "../states/RegisterAction";

/**
 * The reducer for the registration form.
 *
 * @param state The current state of the registration form.
 * @param action The action to be performed.
 */
export default function (state: RegisterState, action: RegisterAction): RegisterState {
    switch (state.tag) {
        case "editing":
            switch (action.type) {
                case "submit":
                    return {tag: "submitting"}
                case "edit":
                    return {tag: "editing", isValid: action.isValid}
                default:
                    throw Error(`Invalid action: ${action} with state: ${state}`)
            }
        case "submitting":
            switch (action.type) {
                case "success":
                    return {tag: "redirect"}
                case "error":
                    return {tag: "error", message: action.message}
                default:
                    throw Error(`Invalid action: ${action} with state: ${state}`)
            }
        case "error":
            switch (action.type) {
                case "go-back":
                    return {tag: "editing", isValid: false}
                default:
                    throw Error(`Invalid action: ${action} with state: ${state}`)
            }
        case "redirect":
            throw Error(`Redirect is a final state.`)
    }
}