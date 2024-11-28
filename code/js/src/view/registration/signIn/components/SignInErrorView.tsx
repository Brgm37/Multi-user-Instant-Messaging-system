import * as React from "react";
import {InputLabelContext} from "../../components/InputLabelContext";
import {InputLabel} from "../../components/InputLabel";
import {SingInFormHandler} from "../hooks/handler/UseSignInFormHandler";

export function SignInErrorView(
    {message, handler}: {
        message: string,
        handler: SingInFormHandler
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
    const confirmPassword = {
        value: loginState.input.confirmPassword,
        onChange: (event: React.ChangeEvent<HTMLInputElement>) => handler.onConfirmPasswordChange(event.target.value),
        error: loginState.error.confirmPasswordError
    }
    const invitationCode = {
        value: loginState.input.invitationCode,
        onChange: (event: React.ChangeEvent<HTMLInputElement>) => handler.onInvitationCodeChange(event.target.value),
        error: loginState.error.invitationCodeError
    }
    return (
        <div>
            <InputLabel label="Username" type="text" disabled={false} input={login}/>
            <InputLabel label="Password" type={"password"} disabled={false} input={password}/>
            <InputLabel label="Confirm Password" type={"password"} disabled={false} input={confirmPassword}/>
            <InputLabel label="Invitation Code" type="text" disabled={false} input={invitationCode}/>
            <button disabled={true}>Submit</button>
            <h2>{message}</h2>
        </div>
    )
}