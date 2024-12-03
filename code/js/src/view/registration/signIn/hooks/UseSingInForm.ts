import * as React from "react"
import {SignInState, makeInitialState} from "./states/SignInState";
import {useContext, useReducer} from "react";
import {SignInValidationResponse} from "../../../../service/registration/signIn/SignInValidationResponse";
import {SignInAction} from "./states/SignInAction";
import {SingInFormHandler} from "./handler/UseSignInFormHandler";
import {SignInServiceContext} from "../../../../service/registration/signIn/SignInServiceContext";
import {setCookie} from "../../../../service/session/SetCookie";
import configJson from "../../../../../envConfig.json";
import {getExpiresIn} from "../../../../service/session/ExpiresTime";

/**
 * The timeout for the validation.
 *
 */
const TIMEOUT = 500

/**
 * The authentication cookie.
 */
const auth_cookie = configJson.session

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
    action: { type: "validation-result", response: SignInValidationResponse }
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
                case "submit":
                    return {tag: "submitting", input: state.input}
                case "validation-result": {
                    const {
                        isUsernameValid,
                        isPasswordValid,
                        isConfirmPasswordValid,
                    } = action.response
                    const error = {
                        usernameError: isUsernameValid === true ? "" : isUsernameValid,
                        passwordError: isPasswordValid === true ? "" : isPasswordValid,
                        confirmPasswordError:
                            isConfirmPasswordValid === true
                                ? ""
                                : state.input.password.confirmPassword.length > 0
                                    ? isConfirmPasswordValid
                                    : "",
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
 * @returns [State, SingInFormHandler]
 */
export function useSignInForm(): [SignInState, SingInFormHandler] {
    const {signIn, stateValidator} = useContext(SignInServiceContext)
    const [state, dispatch] = useReducer(reduce, makeInitialState())
    React.useEffect(() => {
        const timeout = setTimeout(() => {
            if (state.tag !== "editing") return
            stateValidator(state.input.username, state.input.password.password, state.input.password.confirmPassword)
                .then(response => dispatch({type: "validation-result", response}))
        }, TIMEOUT)
        return () => clearTimeout(timeout)
    }, [state, stateValidator])
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
        onSubmit: () => {
            if (state.tag !== "editing") return
            signIn(
                state.input.username,
                state.input.password.password,
                state.input.invitationCode,
            ).then(response => {
                if (response.tag === "success") {
                    const auth = response.value
                    const validity = getExpiresIn(response.value.expirationDate)
                    setCookie(auth_cookie, auth.uId, validity)
                    dispatch({type: "success"})
                }
                else dispatch({type: "error", message: response.value})
            })
            dispatch({type: "submit"})
        }
    }
    return [state, handler]
}