import * as React from "react";
import {InputLabel} from "../../components/InputLabel";
import {InputLabelContext} from "../../components/InputLabelContext";
import {UseLoginFormHandler} from "../hooks/handler/UseLoginFormHandler";


/**
 * The view for the editing state of the login form.
 *
 * @param handler The handler for the login form.
 */
export function LoginEditingView({handler}: {handler: UseLoginFormHandler}): React.JSX.Element {
    const loginState = React.useContext(InputLabelContext)
    const isVisible = loginState.visibility.password ? 'text' : 'password'
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
            <InputLabel label="Password" type={isVisible} disabled={false} input={password}/>
            <button onClick={handler.onSubmit} disabled={!loginState.input.isValid}>Submit</button>
            <button onClick={handler.togglePasswordVisibility}>Toggle Password Visibility</button>
        </div>
    )
}