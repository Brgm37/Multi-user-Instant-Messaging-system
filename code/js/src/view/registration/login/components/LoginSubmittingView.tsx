import * as React from 'react'
import {InputLabelContext} from "../../components/InputLabelContext";
import {InputLabel} from "../../components/InputLabel";

/**
 * The view for the submitting state of the login form.
 */
export function LoginSubmittingView(): React.JSX.Element {
    const loginState = React.useContext(InputLabelContext)
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
    return (
        <div>
            <InputLabel label="Username" type="text" disabled={false} input={login}/>
            <InputLabel label="Password" type={"password"} disabled={false} input={password}/>
            <button disabled={true}>Submit</button>
            <h2>Submitting...</h2>
        </div>
    )
}