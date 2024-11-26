import {useEffect, useReducer} from "react"
import {useFetch} from "../../utils/useFetch"
import {urlBuilder} from "../../utils/UrlBuilder"
import {LoginState, makeInitialState} from "./aux/LoginState"
import {LoginAction, LoginValidationResponse} from "./aux/LoginAction";

/**
 * The timeout for the validation.
 *
 */
const TIMEOUT = 500

/**
 * The URL for the login API.
 */
const loginApiUrl = urlBuilder("/users/login")

/**
 * The header for the username.
 */
const usernameHeader = "username"

/**
 * The header for the password.
 */
const passwordHeader = "password"

/**
 * The reducer function for the login form.
 *
 * @param state
 * @param action
 *
 * @returns State
 */
function reduce(state: LoginState, action: LoginAction): LoginState {
    switch (state.tag) {
        case "editing":
            switch (action.type) {
                case "edit": {
                    const input = {...state.input, [action.inputName]: action.inputValue}
                    return {...state, input}
                }
                case "toggleVisibility": {
                    return {...state, visibility: !state.visibility}
                }
                case "submit": {
                    return {tag: "submitting", input: state.input}
                }
                case "validation-result": {
                    const {isUsernameValid, isPasswordValid} = action.response
                    const error = {
                        usernameError: isUsernameValid === true ? "" : isUsernameValid,
                        passwordError: isPasswordValid === true ? "" : isPasswordValid
                    }
                    const isValid = isUsernameValid === true && isPasswordValid === true
                    const input = {...state.input, isValid}
                    return {...state, error, input}
                }
                default:
                    throw Error("Invalid action")
            }
        case "submitting":
            switch (action.type) {
                case "success":
                    return {tag: "redirect"}
                case "error":
                    return {tag: "error", message: action.message, input: state.input}
                default:
                    throw Error("Invalid action")
            }
        case "error":
            switch (action.type) {
                case "edit":
                    const input = {...state.input, [action.inputName]: action.inputValue}
                    return {tag: "editing", input, visibility: false, error: {usernameError: "", passwordError: ""}}
                default:
                    throw Error("Invalid action")
            }
        case "redirect":
            throw Error("Already in final State 'redirect' and should not reduce to any other State.")
        default:
            throw Error("Invalid state")
    }
}

/**
 * The Handler for the login form.
 */
export type UseLoginFormHandler = {
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

    /**
     * The function to call when the user clicks on the submit button.
     *
     * @returns void
     */
    onSubmit: () => void,
}

/**
 * The hook for the login form.
 *
 * @param stateValidator The validation service.
 *
 * @returns [State, UseLoginFormHandler]
 */
export function useLoginForm(
    stateValidator: (username: string, password: string) => Promise<LoginValidationResponse>,
): [LoginState, UseLoginFormHandler] {
    const [state, dispatch] = useReducer(reduce, makeInitialState())
    const fetchHandler =
        useFetch(
            loginApiUrl,
            "POST",
            response => response.json().then(() => dispatch({type: "success", response})),
            response => dispatch({type: "error", message: response.message}),
            state.tag != "editing"
                ? {"username": "", "password": ""}
                : {"username": state.input.username, "password": state.input.password}
        )
    useEffect(
        () => {
            if (state.tag !== "editing") return
            const timeout: NodeJS.Timeout = setTimeout(() => {
                stateValidator(state.input.username, state.input.password)
                    .then(response => dispatch({type: "validation-result", response}))
                    .catch(err => dispatch({type: "error", message: err.message}))
            }, TIMEOUT)
            return () => clearTimeout(timeout)
        },
        [state, stateValidator]
    )
    const onUsernameChange = (username: string) => {
        fetchHandler.toUpdate(usernameHeader, username)
        dispatch({type: "edit", inputName: "username", inputValue: username})
    }
    const onPasswordChange = (password: string) => {
        fetchHandler.toUpdate(passwordHeader, password)
        dispatch({type: "edit", inputName: "password", inputValue: password})
    }
    const togglePasswordVisibility = () => dispatch({type: "toggleVisibility"})
    const onSubmit = () => {
        dispatch({type: "submit"})
        fetchHandler.toFetch()
    }
    return [state, {onUsernameChange, onPasswordChange, togglePasswordVisibility, onSubmit}]
}
