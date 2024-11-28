import * as React from 'react';
import {InputLabelContext} from "../../components/InputLabelContext";
import {InputLabel} from "../../components/InputLabel";
import {SingInFormHandler} from "../hooks/handler/UseSignInFormHandler";

/**
 * The view for the editing state of the signIn form.
 *
 * @param handler The handler for the signIn form.
 */
export function SignInEditingView({handler}: {handler: SingInFormHandler}): React.JSX.Element {
    const loginState = React.useContext(InputLabelContext);
    const isPasswordVisible = loginState.visibility.password ? 'text' : 'password';
    const isConfirmPasswordVisible = loginState.visibility.confirmPassword ? 'text' : 'password';
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
            <InputLabel label="Password" type={isPasswordVisible} disabled={false} input={password}/>
            <InputLabel label="Confirm Password" type={isConfirmPasswordVisible} disabled={false} input={confirmPassword}/>
            <InputLabel label="Invitation Code" type="text" disabled={false} input={invitationCode}/>
            <button onClick={handler.onSubmit} disabled={!loginState.input.isValid}>Submit</button>
            <button onClick={handler.onPasswordVisibilityToggle}>Toggle Password Visibility</button>
            <button onClick={handler.onConfirmPasswordVisibilityToggle}>Toggle Confirm Password Visibility</button>
        </div>
    )
}