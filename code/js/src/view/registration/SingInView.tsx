import * as React from 'react'
import {useSingInForm, SingInValidationResponse} from "../../service/registration/SingInService";
import {InputLabelContext} from "./componentes/InputLabelContext";
import {InputLabel} from "./componentes/InputLabel";
import {Link} from "react-router-dom";

export function SingInView(
    {validator}: {
        validator: (
            username: string,
            password: string,
            confirmPassword: string,
        ) => Promise<SingInValidationResponse>,
    },
): React.JSX.Element {
    const [singIn, handler] = useSingInForm(validator)
    const usernameInput =
        {
            value: singIn.username,
            onChange: (e: React.ChangeEvent<HTMLInputElement>) => handler.onUsernameChange(e.target.value),
            error: singIn.usernameError
        }
    const passwordInput =
        {
            value: singIn.password,
            onChange: (e: React.ChangeEvent<HTMLInputElement>) => handler.onPasswordChange(e.target.value),
            error: singIn.passwordError
        }
    const confirmPasswordInput =
        {
            value: singIn.confirmPassword,
            onChange: (e: React.ChangeEvent<HTMLInputElement>) => handler.onConfirmPasswordChange(e.target.value),
            error: singIn.confirmPasswordError
        }
    const invitationCodeInput =
        {
            value: singIn.invitationCode,
            onChange: (e: React.ChangeEvent<HTMLInputElement>) => handler.onInvitationCodeChange(e.target.value),
            error: ""
        }
    const toLogin = {
        pathname: "/login",
        search: `?username=${encodeURIComponent(singIn.username)}&password=${encodeURIComponent(singIn.password)}`
    }
    return (
        <div>
            <h1>Sign In</h1>
            <form onSubmit={() => {} }>
                <InputLabelContext.Provider value={usernameInput}>
                    <InputLabel label="Username" type="text"/>
                </InputLabelContext.Provider>
                <InputLabelContext.Provider value={passwordInput}>
                    <InputLabel label="Password" type={singIn.visible.isPasswordVisible ? "text" : "password"}/>
                </InputLabelContext.Provider>
                <InputLabelContext.Provider value={confirmPasswordInput}>
                    <InputLabel label="Confirm Password" type={singIn.visible.isConfirmPasswordVisible ? "text" : "password"}/>
                </InputLabelContext.Provider>
                <InputLabelContext.Provider value={invitationCodeInput}>
                    <InputLabel label="Invitation Code" type="text"/>
                </InputLabelContext.Provider>
                <button type="submit" disabled={!singIn.isValid}>Sign In</button>
            </form>
            <button onClick={handler.onPasswordVisibilityToggle}>
                {singIn.visible.isPasswordVisible ? "Hide Password" : "Show Password"}
            </button>
            <button onClick={handler.onConfirmPasswordVisibilityToggle}>
                {singIn.visible.isConfirmPasswordVisible ? "Hide Confirm Password" : "Show Confirm Password"}
            </button>
            <br/>
            <Link to={toLogin}>Login</Link>
        </div>
    )
}
