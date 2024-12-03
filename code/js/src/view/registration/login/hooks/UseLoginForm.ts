import {useContext, useEffect, useReducer} from "react";
import {LoginState, makeInitialState} from "./states/LoginState";
import {LoginServiceContext} from "../../../../service/registration/login/LoginServiceContext";
import {LoginAction} from "./states/LoginAction";
import {UseLoginFormHandler} from "./handler/UseLoginFormHandler";
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
                    return {tag: "editing", input, error: {usernameError: "", passwordError: ""}}
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
 * The hook for the login form.
 *
 * @returns [State, UseLoginFormHandler]
 */
export function useLoginForm(): [LoginState, UseLoginFormHandler] {
    const {login, stateValidator} = useContext(LoginServiceContext)
    const [state, dispatch] = useReducer(reduce, makeInitialState())
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
    const onUsernameChange = (username: string) => dispatch({type: "edit", inputName: "username", inputValue: username})
    const onPasswordChange = (password: string) => dispatch({type: "edit", inputName: "password", inputValue: password})
    const onSubmit = () => {
        if (state.tag !== "editing") return
        login(
            state.input.username,
            state.input.password,
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
    return [state, {onUsernameChange, onPasswordChange, onSubmit}]
}
