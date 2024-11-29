import {LoginState, makeInitialState} from "./states/LoginState";
import {LoginService, makeDefaultLoginService} from "../../../../service/registration/login/LoginService";
import {LoginAction} from "./states/LoginAction";
import {UseLoginFormHandler} from "./handler/UseLoginFormHandler";
import {useEffect, useReducer} from "react";

/**
 * The timeout for the validation.
 *
 */
const TIMEOUT = 500

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
 * The hook for the login form.
 *
 * @param service The login service.
 *
 * @returns [State, UseLoginFormHandler]
 */
export function useLoginForm(
    {login, stateValidator}: LoginService = makeDefaultLoginService(),
): [LoginState, UseLoginFormHandler] {
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
    const togglePasswordVisibility = () => dispatch({type: "toggleVisibility"})
    const onSubmit = () => {
        if (state.tag !== "editing") return
        login(
            state.input.username,
            state.input.password,
            response => response.json().then(() => dispatch({type: "success", response})),
            error => dispatch({type: "error", message: error.message})
            )
        dispatch({type: "submit"})
    }
    return [state, {onUsernameChange, onPasswordChange, togglePasswordVisibility, onSubmit}]
}
