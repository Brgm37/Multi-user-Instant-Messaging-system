import * as React from "react";
import {InputLabelContext} from "../../components/InputLabelContext";
import {InputLabel} from "../../components/InputLabel";
import {UseLoginFormHandler} from "../hooks/handler/UseLoginFormHandler";


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
        <div>
            <InputLabel label="Username" type="text" disabled={false} input={login}/>
            <InputLabel label="Password" type={"password"} disabled={false} input={password}/>
            <button disabled={true}>Submit</button>
            <h2>{message}</h2>
        </div>
    )
}