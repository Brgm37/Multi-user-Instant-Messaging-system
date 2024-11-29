import * as React from 'react';
import {InputLabelContext} from "../../components/InputLabelContext";
import {InputLabel} from "../../components/InputLabel";

/**
 * View for when the user is submitting the signIn form.
 */
export function SignInSubmittingView(): React.JSX.Element {
    const loginState = React.useContext(InputLabelContext);
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
    const confirmPassword = {
        value: loginState.input.confirmPassword,
        onChange: (_: React.ChangeEvent<HTMLInputElement>) => {throw Error('onChange should not be called')},
        error: loginState.error.confirmPasswordError
    }
    const invitationCode = {
        value: loginState.input.invitationCode,
        onChange: (_: React.ChangeEvent<HTMLInputElement>) => {throw Error('onChange should not be called')},
        error: loginState.error.invitationCodeError
    }
    return (
        <div>
            <InputLabel label="Username" type="text" disabled={false} input={login}/>
            <InputLabel label="Password" type={"password"} disabled={false} input={password}/>
            <InputLabel label="Confirm Password" type={"password"} disabled={false} input={confirmPassword}/>
            <InputLabel label="Invitation Code" type="text" disabled={false} input={invitationCode}/>
            <button disabled={true}>Submit</button>
            <h2>Submitting...</h2>
        </div>
    )
}