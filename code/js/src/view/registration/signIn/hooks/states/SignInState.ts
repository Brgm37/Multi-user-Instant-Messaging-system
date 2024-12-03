import {useSearchParams} from "react-router-dom";
import {extractSearchParams} from "../../../../../service/utils/extractParams";

/**
 * The password object.
 *
 * @type Password
 * @prop password The password entered by the user.
 * @prop confirmPassword The password confirmation entered by the user.
 */
type Password = {
    password: string,
    confirmPassword: string,
}

/**
 * The error for the login form.
 *
 * @type InputError
 * @prop usernameError The error message for the username field.
 * @prop passwordError The error message for the password field.
 * @prop confirmPasswordError The error message for the password confirmation field.
 */
type InputError = {
    usernameError: string,
    passwordError: string,
    confirmPasswordError: string,
}
/**
 * The input for the login form.
 *
 * @type UserInput
 * @prop username The username entered by the user.
 * @prop password The password entered by the user.
 * @prop invitationCode The invitation code entered by the user.
 * @prop isValid The validation result for the form.
 */
type UserInput = {
    username: string,
    password: Password,
    invitationCode: string,
    isValid: boolean,
}

/**
 * The state for the login form.
 *
 * @type SignInState
 * @prop tag The tag for the state.
 * @prop input The input for the form.
 * @prop visibility The visibility of the password.
 * @prop error The error for the form.
 */
export type SignInState =
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
 * The initial state for the signIn form.
 *
 * @returns SignInState
 */
export function makeInitialState(): SignInState {
    const [searchParams] = useSearchParams()
    return {
        tag: "editing",
        input: {
            username: extractSearchParams(searchParams.get(usernameParam)),
            password: { password: extractSearchParams(searchParams.get(passwordParam)), confirmPassword: "" },
            invitationCode: "",
            isValid: false,
        },
        error: { usernameError: "", passwordError: "", confirmPasswordError: ""},
    }
}