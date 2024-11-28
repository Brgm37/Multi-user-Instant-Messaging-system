import {useEffect} from "react"
import * as React from "react"
import {useSearchParams} from "react-router-dom";

/**
 * @description Type for the state of the login form.
 *
 * @type State
 * @prop username The username entered by the user.
 * @prop password The password entered by the user.
 * @prop isValid The validation result for the form.
 * @prop usernameError The error message for the username field.
 * @prop passwordError The error message for the password field.
 */
type State = {
    username: string,
    password: string,
    isValid: boolean,
    isPasswordVisible: boolean,
    usernameError: string,
    passwordError: string,
}

/**
 * The reducer function for the login form.
 *
 * @prop type The type of the action.
 * @prop username The username entered by the user.
 * @prop password The password entered by the user.
 * @prop isUsernameValid The validation result for the username field.
 * @prop isPasswordValid The validation result for the password field.
 */
type Action =
    { type: "setUsername", username: string } |
    { type: "setPassword", password: string } |
    { type: "togglePasswordVisibility" } |
    { type: "validation-result", response: LoginValidationResponse }

/**
 * The reducer function for the login form.
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
        case "validation-result":
            return {
                ...state,
                usernameError: action.response.isUsernameValid === true ? undefined : action.response.isUsernameValid,
                passwordError: action.response.isPasswordValid === true ? undefined : action.response.isPasswordValid,
                isValid: action.response.isUsernameValid === true && action.response.isPasswordValid === true
            }
        case "togglePasswordVisibility":
            return {...state, isPasswordVisible: !state.isPasswordVisible}
    }
}

/**
 * The response from the validation service.
 *
 * @type LoginValidationResponse
 * @prop isUsernameValid The validation result for the username field.
 * @prop isPasswordValid The validation result for the password field.
 */
export type LoginValidationResponse = {
    isUsernameValid: string | true,
    isPasswordValid: string | true
}

/**
 * The Handler for the login form.
 */
type UseLoginFormHandler = {
    /**
     * The function to call when the username changes.
     * @param username The new username.
     * @returns void
     */
    onUsernameChange: (username: string) => void,
    /**
     * The function to call when the password changes.
     * @param password The new password.
     * @returns void
     */
    onPasswordChange: (password: string) => void,

    /**
     * The function to call when the user clicks on the password visibility button.
     *
     * @returns void
     */
    togglePasswordVisibility: () => void,
}

/**
 * The timeout for the validation.
 *
 */
const TIMEOUT = 500

/**
 * The hook for the login form.
 *
 * @param stateValidator The validation service.
 *
 * @returns [State, UseLoginFormHandler]
 */
export function useLoginForm(
    stateValidator: (username: string, password: string) => Promise<LoginValidationResponse>,
): [State, UseLoginFormHandler] {
    const [searchParams] = useSearchParams()
    const [state, dispatch] = React.useReducer(reduce, {
        username: decodeURIComponent(searchParams.get("username")) || "",
        password: decodeURIComponent(searchParams.get("password")) || "",
        isValid: false,
        isPasswordVisible: false,
        usernameError: "",
        passwordError: "",
    })
    useEffect(
        () => {
            const timeout: NodeJS.Timeout = setTimeout(() => {
                stateValidator(state.username, state.password)
                    .then(response => dispatch({type: "validation-result", response}))
            }, TIMEOUT)
            return () => clearTimeout(timeout)
        },
        [state.username, state.password, stateValidator]
    )
    const onUsernameChange = (username: string) => dispatch({type: "setUsername", username})
    const onPasswordChange = (password: string) => dispatch({type: "setPassword", password})
    const togglePasswordVisibility = () => dispatch({type: "togglePasswordVisibility"})
    return [state, {
        onUsernameChange,
        onPasswordChange,
        togglePasswordVisibility,
    }]
}
