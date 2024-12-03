import * as React from "react";
import {InputLabelContext} from "../../components/InputLabelContext";
import {InputLabel} from "../../components/InputLabel";
import {SingInFormHandler} from "../hooks/handler/UseSignInFormHandler";
import {SignInBaseView} from "./SignInBaseView";

export function SignInErrorView(
    {message, handler}: {
        message: string,
        handler: SingInFormHandler
    }
): React.JSX.Element {
    const signInState = React.useContext(InputLabelContext)
    const signIn = {
        value: signInState.input.username,
        onChange: (event: React.ChangeEvent<HTMLInputElement>) => handler.onUsernameChange(event.target.value),
        error: signInState.error.usernameError
    }
    const password = {
        value: signInState.input.password,
        onChange: (event: React.ChangeEvent<HTMLInputElement>) => handler.onPasswordChange(event.target.value),
        error: signInState.error.passwordError
    }
    const confirmPassword = {
        value: signInState.input.confirmPassword,
        onChange: (event: React.ChangeEvent<HTMLInputElement>) => handler.onConfirmPasswordChange(event.target.value),
        error: signInState.error.confirmPasswordError
    }
    const invitationCode = {
        value: signInState.input.invitationCode,
        onChange: (event: React.ChangeEvent<HTMLInputElement>) => handler.onInvitationCodeChange(event.target.value),
        error: signInState.error.invitationCodeError
    }
    return (
        <SignInBaseView
            signIn={signIn}
            password={password}
            confirmPassword={confirmPassword}
            invitationCode={invitationCode}
            inputsDisabled={false}
            isValid={signInState.input.isValid}
            error={message}>
        </SignInBaseView>
    )
}