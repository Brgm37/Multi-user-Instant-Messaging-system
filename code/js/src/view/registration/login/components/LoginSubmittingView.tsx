import * as React from 'react'
import {InputLabelContext} from "../../components/InputLabelContext";
import {InputLabel} from "../../components/InputLabel";
import {LoginBaseView} from "./LoginBaseView";

/**
 * The view for the submitting state of the login form.
 */
export function LoginSubmittingView(): React.JSX.Element {
    const loginState = React.useContext(InputLabelContext)
    const login = {
        value: loginState.input.username,
        onChange: (_: React.ChangeEvent<HTMLInputElement>) => {throw Error('onChange should not be called')},
        error: loginState.error.usernameError
    }
    const password = {
        value: loginState.input.password,
        onChange: (_: React.ChangeEvent<HTMLInputElement>) => {throw Error('onChange should not be called')},
        error: loginState.error.passwordError
    }
    return (
        <LoginBaseView
            login={login}
            password={password}
            loginState={loginState}
            inputsDisabled={true}
        />
    )
}