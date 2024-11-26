import * as React from "react"
import {SignInAction, SignInValidationResponse} from "./aux/SignInAction";
import {SignInState, makeInitialState} from "./aux/SignInState";
import {useReducer} from "react";
import {useFetch} from "../../utils/useFetch";
import {urlBuilder} from "../../utils/UrlBuilder";

/**
 * The timeout for the validation service.
 *
 */
const TIMEOUT = 500

/**
 * The URL for the SignIn API.
 */
const signInApiUrl = urlBuilder("/users/signup")

/**
 * The header for the username.
 */
const invitationCodeHeader = "invitationCode"

/**
 * The header for the password.
 */
const passwordHeader = "password"

/**
 * The header for the password confirmation.
 */
const usernameHeader = "username"

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
    state: SignInState,
    action: {type: "validation-result", response: SignInValidationResponse}
): boolean {
    if (state.tag !== "editing") return false
    return action.response.isUsernameValid === true &&
        action.response.isPasswordValid === true &&
        action.response.isConfirmPasswordValid === true &&
        state.input.password.password === state.input.password.confirmPassword &&
        state.input.invitationCode.length > 0
}

/**
 * The reducer function for the SignIn form.
 *
 * @param state
 * @param action
 *
 * @returns State
 */
function reduce(state: SignInState, action: SignInAction): SignInState {
    switch (state.tag) {
        case "editing":
            switch (action.type) {
                case "edit": {
                    switch (action.inputName) {
                        case "password": {
                            const password = {
                                password: action.inputValue,
                                confirmPassword: state.input.password.confirmPassword
                            }
                            const input = {...state.input, password}
                            return {...state, input}
                        }
                        case "confirmPassword": {
                            const confirmPassword = {
                                password: state.input.password.password,
                                confirmPassword: action.inputValue
                            }
                            const input = {...state.input, password: confirmPassword}
                            return {...state, input}
                        }
                        default: {
                            const input = {...state.input, [action.inputName]: action.inputValue}
                            return {...state, input}
                        }
                    }
                }
                case "toggleVisibility": {
                    const visibility =
                        {
                            ...state.visibility,
                            [action.inputName]: !state.visibility[action.inputName]
                        }
                    return {...state, visibility}
                }
                case "submit":
                    return {tag: "submitting", input: state.input}
                case "validation-result": {
                    const {isUsernameValid, isPasswordValid, isConfirmPasswordValid} = action.response
                    const error = {
                        usernameError: isUsernameValid === true ? "" : isUsernameValid,
                        passwordError: isPasswordValid === true ? "" : isPasswordValid,
                        confirmPasswordError:
                            isConfirmPasswordValid === true
                                ? ""
                                : state.input.password.confirmPassword.length > 0
                                    ? isConfirmPasswordValid
                                    : ""
                    }
                    const isValid = isValidForm(state, action)
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
                case "edit": {
                    const input = {...state.input, [action.inputName]: action.inputValue}
                    return {tag: "editing", input, visibility: {password: false, confirmPassword: false}, error: {usernameError: "", passwordError: "", confirmPasswordError: ""}}
                }
                default:
                    throw Error("Invalid action")
            }
        case "redirect":
            throw Error("Already in final State 'redirect' and should not reduce to any other State.")
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
export type SingInFormHandler = {
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

    /**
     * The handler for the form submission event.
     */
    onSubmit: () => void

}

/**
 * The hook for the SignIn form.
 *
 * @param stateValidator The validator function for the form.
 *
 * @returns [State, SingInFormHandler]
 */
export function useSignInForm(
    stateValidator:
        (
            username: string,
            password: string,
            confirmPassword: string,
        ) => Promise<SignInValidationResponse>,
): [SignInState, SingInFormHandler] {
    const [state, dispatch] = useReducer(reduce, makeInitialState())
    const fetchHandler = useFetch(
        signInApiUrl,
        "POST",
        response => response.json().then(() => dispatch({type: "success", response})),
        response => dispatch({type: "error", message: response.message}),
        state.tag !== "editing"
            ? {"username": "", "password": "", "invitationCode": ""}
            : {
                "username": state.input.username,
                "password": state.input.password.password,
                "invitationCode": state.input.invitationCode
            }
    )
    const handler: SingInFormHandler = {
        onUsernameChange: (username: string) => {
            dispatch({type: "edit", inputName: "username", inputValue: username})
            fetchHandler.toUpdate(usernameHeader, username)
        },
        onPasswordChange: (password: string) => {
            dispatch({type: "edit", inputName: "password", inputValue: password})
            fetchHandler.toUpdate(passwordHeader, password)
        },
        onConfirmPasswordChange: (confirmPassword: string) => {
            dispatch({type: "edit", inputName: "confirmPassword", inputValue: confirmPassword})
        },
        onInvitationCodeChange: (invitationCode: string) => {
            dispatch({type: "edit", inputName: "invitationCode", inputValue: invitationCode})
            fetchHandler.toUpdate(invitationCodeHeader, invitationCode)
        },
        onPasswordVisibilityToggle: () =>
            dispatch({type: "toggleVisibility", inputName: "password"}),
        onConfirmPasswordVisibilityToggle: () =>
            dispatch({type: "toggleVisibility", inputName: "confirmPassword"}),
        onSubmit: () => {
            dispatch({type: "submit"})
            fetchHandler.toFetch
        }
    }
    React.useEffect(() => {
        const timeout = setTimeout(() => {
            if (state.tag !== "editing") return
            stateValidator(state.input.username, state.input.password.password, state.input.password.confirmPassword)
                .then(response => dispatch({type: "validation-result", response}))
        }, TIMEOUT)
        return () => clearTimeout(timeout)
    }, [state, stateValidator])
    return [state, handler]
}