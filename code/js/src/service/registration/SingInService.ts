import * as React from "react"
import {useSearchParams} from "react-router-dom";

/**
 * @description Type for the state of the SignIn component
 *
 * @type State
 * @prop username The username entered by the user.
 * @prop password The password entered by the user.
 * @prop confirmPassword The password confirmation entered by the user.
 * @prop invitationCode The invitation code entered by the user.
 * @prop isValid The validation result for the form.
 * @prop usernameError The error message for the username field.
 * @prop passwordError The error message for the password field.
 * @prop confirmPasswordError The error message for the password confirmation field.
 */
type State = {
    username: string
    password: string,
    confirmPassword: string,
    invitationCode: string,
    isValid: boolean,
    visible: Visibility,
    usernameError: string,
    passwordError: string,
    confirmPasswordError: string,
}

/**
 * The visibility state of the password fields.
 *
 * @type Visibility
 * @prop isPasswordVisible The visibility state of the password field.
 * @prop isConfirmPasswordVisible The visibility state of the password confirmation field.
 */
type Visibility = {
    isPasswordVisible: boolean,
    isConfirmPasswordVisible: boolean,
}

/**
 * The reducer function for the SignIn form.
 *
 * @prop type The type of the action.
 * @prop username The username entered by the user.
 * @prop password The password entered by the user.
 * @prop confirmPassword The password confirmation entered by the user.
 * @prop invitationCode The invitation code entered by the user.
 * @prop isUsernameValid The validation result for the username field.
 */
type Action =
    { type: "setUsername", username: string } |
    { type: "setPassword", password: string } |
    { type: "setConfirmPassword", confirmPassword: string } |
    { type: "setInvitationCode", invitationCode: string } |
    { type: "toggle-password-visibility" } |
    { type: "toggle-confirm-password-visibility" } |
    { type: "validation-result", response: SingInValidationResponse }

/**
 * The response from the validation service.
 *
 * @type SingInValidationResponse
 * @prop isUsernameValid The validation result for the username field.
 * @prop isPasswordValid The validation result for the password field.
 * @prop isConfirmPasswordValid The validation result for the password confirmation field.
 */
export type SingInValidationResponse = {
    isUsernameValid: string | true,
    isPasswordValid: string | true,
    isConfirmPasswordValid: string | true,
}

/**
 * The verifier function for the SignIn form.
 *
 * Verifies if the form is valid.
 *
 * @param state
 * @param action
 *
 * @returns boolean
 */
function isValidForm(
    state: State,
    action: {type: "validation-result", response: SingInValidationResponse}
): boolean {
    return action.response.isUsernameValid === true &&
        action.response.isPasswordValid === true &&
        action.response.isConfirmPasswordValid === true &&
        state.password === state.confirmPassword &&
        state.invitationCode.length > 0
}

/**
 * The reducer function for the SignIn form.
 *
 * @param state
 * @param action
 *
 * @returns State
 */
function reduce(state: State, action: Action): State {
    switch (action.type) {
        case "setPassword":
            return {...state, password: action.password}
        case "setUsername":
            return {...state, username: action.username}
        case "setConfirmPassword":
            return {...state, confirmPassword: action.confirmPassword}
        case "setInvitationCode":
            return {...state, invitationCode: action.invitationCode}
        case "validation-result":
            return {
                ...state,
                usernameError: action.response.isUsernameValid === true ? undefined : action.response.isUsernameValid,
                passwordError: action.response.isPasswordValid === true ? undefined : action.response.isPasswordValid,
                confirmPasswordError: action.response.isConfirmPasswordValid === true ? undefined : action.response.isConfirmPasswordValid,
                isValid: isValidForm(state, action)
            }
        case "toggle-password-visibility":
            return {
                ...state,
                visible: {
                    isPasswordVisible: !state.visible.isPasswordVisible,
                    isConfirmPasswordVisible: state.visible.isConfirmPasswordVisible
                }
            }
        case "toggle-confirm-password-visibility":
            return {
                ...state,
                visible: {
                    isPasswordVisible: state.visible.isPasswordVisible,
                    isConfirmPasswordVisible: !state.visible.isConfirmPasswordVisible
                }
            }
    }
}

/**
 * The handler for the SignIn form.
 *
 * @type SingInFormHandler
 *
 * @prop onUsernameChange The handler for the username change event.
 * @prop onPasswordChange The handler for the password change event.
 * @prop onConfirmPasswordChange The handler for the password confirmation change event.
 * @prop onInvitationCodeChange The handler for the invitation code change event.
 */
type SingInFormHandler = {
    /**
     * The handler for the username change event.
     * @param username
     */
    onUsernameChange: (username: string) => void,
    /**
     * The handler for the password change event.
     * @param password
     */
    onPasswordChange: (password: string) => void,
    /**
     * The handler for the password confirmation change event.
     * @param confirmPassword
     */
    onConfirmPasswordChange: (confirmPassword: string) => void,
    /**
     * The handler for the invitation code change event.
     * @param invitationCode
     */
    onInvitationCodeChange: (invitationCode: string) => void,

    /**
     * The handler for the password visibility toggle event.
     */
    onPasswordVisibilityToggle: () => void,

    /**
     * The handler for the confirmation password visibility toggle event.
     */
    onConfirmPasswordVisibilityToggle: () => void,

}

/**
 * The timeout for the validation service.
 *
 */
const TIMEOUT = 500

/**
 * The hook for the SignIn form.
 *
 * @param stateValidator The validator function for the form.
 *
 * @returns [State, SingInFormHandler]
 */
export function useSingInForm(
    stateValidator:
        (
            username: string,
            password: string,
            confirmPassword: string,
        ) => Promise<SingInValidationResponse>,
): [State, SingInFormHandler] {
    const [searchParams] = useSearchParams()
    const [state, dispatch] = React.useReducer(reduce, {
        username: decodeURIComponent(searchParams.get("username")) || "",
        password: decodeURIComponent(searchParams.get("password")) || "",
        confirmPassword: "",
        invitationCode: "",
        isValid: false,
        visible: {
            isPasswordVisible: false,
            isConfirmPasswordVisible: false,
        },
        usernameError: "",
        passwordError: "",
        confirmPasswordError: "",
    })
    const handler: SingInFormHandler = {
        onUsernameChange: (username: string) => dispatch({type: "setUsername", username}),
        onPasswordChange: (password: string) => dispatch({type: "setPassword", password}),
        onConfirmPasswordChange: (confirmPassword: string) => dispatch({type: "setConfirmPassword", confirmPassword}),
        onInvitationCodeChange: (invitationCode: string) => dispatch({type: "setInvitationCode", invitationCode}),
        onPasswordVisibilityToggle: () => dispatch({type: "toggle-password-visibility"}),
        onConfirmPasswordVisibilityToggle: () => dispatch({type: "toggle-confirm-password-visibility"}),
    }
    React.useEffect(() => {
        const timeout = setTimeout(() => {
            stateValidator(state.username, state.password, state.confirmPassword)
                .then(response => dispatch({type: "validation-result", response}))
        }, TIMEOUT)
        return () => clearTimeout(timeout)
    }, [state.username, state.password, state.confirmPassword, state.invitationCode, stateValidator])
    return [state, handler]
}