import * as React from 'react'
import {useSignInForm} from "../../../service/registration/signIn/SingInService";
import {SignInValidationResponse} from "../../../service/registration/signIn/states/SignInAction";
import {Link, Navigate, useLocation} from "react-router-dom";
import { InputLabelContext } from '../components/InputLabelContext';
import {SignInEditingView} from "./components/SignInEditingView";
import {SignInSubmittingView} from "./components/SignInSubmittingView";
import {SignInErrorView} from "./components/SignInErrorView";
import {SignInState} from "../../../service/registration/signIn/states/SignInState";

export function SignInView(
    {validator}: {
        validator: (
            username: string,
            password: string,
            confirmPassword: string,
        ) => Promise<SignInValidationResponse>,
    },
): React.JSX.Element {
    const [signIn, handler] = useSignInForm(validator)
    const location = useLocation()
    if (signIn.tag === "redirect") {
        let source = location.state?.source
        if (!source) source = "/home"
        return <Navigate to={source} replace={true}></Navigate>
    }
    let visibility
    let error
    if (signIn.tag == "editing") {
        visibility = {password: signIn.visibility.password, confirmPassword: signIn.visibility.confirmPassword}
        error = {
            usernameError: signIn.error.usernameError,
            passwordError: signIn.error.passwordError,
            confirmPasswordError: signIn.error.confirmPasswordError,
        }
    }
    const input = {
        username: signIn.input.username,
        password: signIn.input.password.password,
        isValid: signIn.input.isValid,
        confirmPassword: signIn.input.password.confirmPassword,
        invitationCode: signIn.input.invitationCode,
    }
    const form = {
        input,
        visibility,
        error,
    }
    const view = ((s: SignInState) => {
        switch (s.tag) {
            case "editing":
                return <SignInEditingView handler={handler}/>
            case "submitting":
                return <SignInSubmittingView/>
            case "error":
                return <SignInErrorView message={s.message} handler={handler}/>
        }
    })
    return (
        <InputLabelContext.Provider value={form}>
            <div>
                <h1>Sign In</h1>
                {view(signIn)}
                <Link to={
                    {
                        pathname:"/login",
                        search:`?username=${signIn.input.username}&password=${signIn.input.password.password}`
                    }
                }>Login</Link>
            </div>
        </InputLabelContext.Provider>
    )
}
