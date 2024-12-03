import {useSearchParams} from "react-router-dom";
import {extractSearchParams} from "../../../../../service/utils/extractParams";

/**
 * The input for the login form.
 *
 * @type UserInput
 * @prop username The username entered by the user.
 * @prop password The password entered by the user.
 * @prop isPasswordVisible The visibility of the password.
 * @prop isValid The validation result for the form.
 */
type UserInput = {
    username: string,
    password: string,
    isPasswordVisible: boolean,
    isValid: boolean,
}

/**
 * The error for the login form.
 *
 * @type InputError
 * @prop usernameError The error message for the username field.
 * @prop passwordError The error message for the password field.
 */
type InputError = {
    usernameError: string,
    passwordError: string,
}

/**
 * The state for the login form.
 *
 * @type LoginState
 * @prop tag The tag for the state.
 * @prop input The input for the form.
 * @prop visibility The visibility of the password.
 * @prop error The error for the form.
 */
export type LoginState =
    { tag: "editing", input: UserInput, error?: InputError } |
    { tag: "error", message: string, input: UserInput } |
    { tag: "submitting", input: UserInput } |
    { tag: "redirect" }


/**
 * The parameters for the login form.
 */
const usernameParam = "username"
/**
 * The parameters for the login form.
 */
const passwordParam = "password"

/**
 * Make the initial state for the login form.
 *
 * @returns LoginState
 */
export function makeInitialState(): LoginState {
    const [searchParams] = useSearchParams()
    return {
        tag: "editing",
        input: {
            username: extractSearchParams(searchParams.get(usernameParam)),
            password: extractSearchParams(searchParams.get(passwordParam)),
            isPasswordVisible: false,
            isValid: false
        },
        error: {
            usernameError: "",
            passwordError: ""
        },
    }
}