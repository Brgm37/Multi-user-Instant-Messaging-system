import * as React from "react";
import {InputLabelContext} from "../../components/InputLabelContext";
import {InputLabel} from "../../components/InputLabel";
import {UseLoginFormHandler} from "../hooks/handler/UseLoginFormHandler";
import {LoginBaseView} from "./LoginBaseView";


export function LoginErrorView(
    {message, handler}: {
        message: string,
        handler: UseLoginFormHandler
    }
): React.JSX.Element {
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
            inputsDisabled={false}
            error={message}
        />
    )
}