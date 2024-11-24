import * as React from 'react'
import {useLoginForm, LoginValidationResponse} from '../../service/registration/LoginService'
import {InputLabelContext} from "./componentes/InputLabelContext";
import {InputLabel} from "./componentes/InputLabel";
import {ChangeEvent} from "react";
import {Link} from "react-router-dom";

export function LoginView(
    {validator}: {
        validator: (username: string, password: string) => Promise<LoginValidationResponse>,
    }
): React.JSX.Element {
    const [login, handler] = useLoginForm(validator)
    const usernameInput =
        {
            value: login.username,
            onChange: (e: ChangeEvent<HTMLInputElement>) => handler.onUsernameChange(e.target.value),
            error: login.usernameError
        }
    const passwordInput =
        {
            value: login.password,
            onChange: (e: ChangeEvent<HTMLInputElement>) => handler.onPasswordChange(e.target.value),
            error: login.passwordError
        }
    const toSignIn = {
        pathname: "/signIn",
        search: `?username=${encodeURIComponent(login.username)}&password=${encodeURIComponent(login.password)}`
    }
    return (
        <div>
            <h1>Login</h1>
            <form onSubmit={() => {} }>
                <InputLabelContext.Provider value={usernameInput}>
                    <InputLabel label="Username" type="text"/>
                </InputLabelContext.Provider>
                <InputLabelContext.Provider value={passwordInput}>
                    <InputLabel label="Password" type={login.isPasswordVisible ? "text" : "password"}/>
                </InputLabelContext.Provider>
                <button type="submit" disabled={!login.isValid}>Login</button>
            </form>
            <button onClick={handler.togglePasswordVisibility}>
                {login.isPasswordVisible ? "Hide Password" : "Show Password"}
            </button>
            <br/>
            <Link to={toSignIn}>Sign In</Link>
        </div>
    )
}