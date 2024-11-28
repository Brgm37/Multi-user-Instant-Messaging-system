import * as React from "react"
import {SignInState, makeInitialState} from "./states/SignInState";
import {useReducer} from "react";
import {makeDefaultSignInService, SignInService} from "../../../../service/registration/signIn/SignInService";
import {SignInValidationResponse} from "../../../../service/registration/signIn/SignInValidationResponse";
import {SignInAction} from "./states/SignInAction";
import {SingInFormHandler} from "./handler/UseSignInFormHandler";

/**
 * The timeout for the validation service.
 *
 */
const TIMEOUT = 500

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
                            const input = {
                                ...state.input,
                                [action.inputName]: action.inputValue
                            }
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
                    const {
                        isUsernameValid,
                        isPasswordValid,
                        isConfirmPasswordValid
                    } = action.response
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
                    return {
                        tag: "editing",
                        input,
                        visibility: {password: false, confirmPassword: false},
                        error: {usernameError: "", passwordError: "", confirmPasswordError: ""}
                    }
                }
                default:
                    throw Error("Invalid action")
            }
        case "redirect":
            throw Error("Already in final State 'redirect' and should not reduce to any other State.")
    }
}

/**
 * The hook for the SignIn form.
 *
 * @param service The service for the SignIn form.
 *
 * @returns [State, SingInFormHandler]
 */
export function useSignInForm(
    {signIn, stateValidator}: SignInService = makeDefaultSignInService(),
): [SignInState, SingInFormHandler] {
    const [state, dispatch] = useReducer(reduce, makeInitialState())
    const handler: SingInFormHandler = {
        onUsernameChange: (username: string) => {
            dispatch({type: "edit", inputName: "username", inputValue: username})
        },
        onPasswordChange: (password: string) => {
            dispatch({type: "edit", inputName: "password", inputValue: password})
        },
        onConfirmPasswordChange: (confirmPassword: string) => {
            dispatch({type: "edit", inputName: "confirmPassword", inputValue: confirmPassword})
        },
        onInvitationCodeChange: (invitationCode: string) => {
            dispatch({type: "edit", inputName: "invitationCode", inputValue: invitationCode})
        },
        onPasswordVisibilityToggle: () =>
            dispatch({type: "toggleVisibility", inputName: "password"}),
        onConfirmPasswordVisibilityToggle: () =>
            dispatch({type: "toggleVisibility", inputName: "confirmPassword"}),
        onSubmit: () => {
            if (state.tag !== "editing") return
            dispatch({type: "submit"})
            signIn(
                state.input.username,
                state.input.password.password,
                state.input.invitationCode,
                response => response.json().then(() => dispatch({type: "success", response})),
                error => dispatch({type: "error", message: error.message})
            )
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