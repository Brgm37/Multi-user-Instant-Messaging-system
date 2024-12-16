import {LoginState} from "../states/LoginState";
import {LoginAction} from "../states/LoginAction";

/**
 * The reducer for the login form.
 *
 *
 * @param state The current state of the login form.
 * @param action The action to be performed.
 */
export default function (state: LoginState, action: LoginAction): LoginState {
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