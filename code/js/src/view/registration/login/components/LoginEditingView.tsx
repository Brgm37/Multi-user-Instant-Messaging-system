import * as React from "react";
import {InputLabel} from "../../components/InputLabel";
import {InputLabelContext} from "../../components/InputLabelContext";
import {UseLoginFormHandler} from "../hooks/handler/UseLoginFormHandler";
import {Link} from "react-router-dom";
import {LoginBaseView} from "./LoginBaseView";


/**
 * The view for the editing state of the login form.
 *
 * @param handler The handler for the login form.
 */
export function LoginEditingView({handler}: {handler: UseLoginFormHandler}): React.JSX.Element {
    const loginState = React.useContext(InputLabelContext)
    const login = {
        value: loginState.input.username,
        onChange: (event: React.ChangeEvent<HTMLInputElement>) => handler.onUsernameChange(event.target.value),
        error: loginState.error.usernameError
    }
    const password = {
        value: loginState.input.password,
        onChange: (event: React.ChangeEvent<HTMLInputElement>) => handler.onPasswordChange(event.target.value),
        error: loginState.error.passwordError
    }
    return (
        <LoginBaseView
            login={login}
            password={password}
            loginState={loginState}
            onSubmit={handler.onSubmit}
            inputsDisabled={false}
        />
    )
}
