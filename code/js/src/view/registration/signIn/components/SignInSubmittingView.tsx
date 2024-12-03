import * as React from 'react';
import {InputLabelContext} from "../../components/InputLabelContext";
import {InputLabel} from "../../components/InputLabel";
import {SignInBaseView} from "./SignInBaseView";

/**
 * View for when the user is submitting the signIn form.
 */
export function SignInSubmittingView(): React.JSX.Element {
    const signInState = React.useContext(InputLabelContext);
    const signIn = {
        value: signInState.input.username,
        onChange: (_: React.ChangeEvent<HTMLInputElement>) => {
            throw Error('onChange should not be called')
        },
        error: signInState.error.usernameError
    }
    const password = {
        value: signInState.input.password,
        onChange: (_: React.ChangeEvent<HTMLInputElement>) => {
            throw Error('onChange should not be called')
        },
        error: signInState.error.passwordError
    }
    const confirmPassword = {
        value: signInState.input.confirmPassword,
        onChange: (_: React.ChangeEvent<HTMLInputElement>) => {
            throw Error('onChange should not be called')
        },
        error: signInState.error.confirmPasswordError
    }
    const invitationCode = {
        value: signInState.input.invitationCode,
        onChange: (_: React.ChangeEvent<HTMLInputElement>) => {
            throw Error('onChange should not be called')
        },
        error: signInState.error.invitationCodeError
    }
    return (
        <SignInBaseView
            signIn={signIn}
            password={password}
            confirmPassword={confirmPassword}
            invitationCode={invitationCode}
            inputsDisabled={true}
            isValid={signInState.input.isValid}
        />
    )
}