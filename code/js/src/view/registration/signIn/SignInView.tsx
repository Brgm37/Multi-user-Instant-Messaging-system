import * as React from 'react'
import {Link, Navigate, useLocation} from "react-router-dom";
import {InputLabelContext} from '../components/InputLabelContext';
import {SignInEditingView} from "./components/SignInEditingView";
import {SignInSubmittingView} from "./components/SignInSubmittingView";
import {SignInErrorView} from "./components/SignInErrorView";
import {SignInState} from "./hooks/states/SignInState";
import {useSignInForm} from "./hooks/UseSingInForm";

export function SignInView(): React.JSX.Element {
    const [signInState, handler] = useSignInForm()
    const location = useLocation()
    if (signInState.tag === "redirect") {
        let source = location.state?.source
        if (!source) source = "/channels"
        return <Navigate to={source} replace={true}></Navigate>
    }
    let error
    if (signInState.tag == "editing") {
        error = {
            usernameError: signInState.error.usernameError,
            passwordError: signInState.error.passwordError,
            confirmPasswordError: signInState.error.confirmPasswordError,
        }
    }
    const input = {
        username: signInState.input.username,
        password: signInState.input.password.password,
        isValid: signInState.input.isValid,
        confirmPassword: signInState.input.password.confirmPassword,
        invitationCode: signInState.input.invitationCode,
    }
    const form = {
        input,
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
                {view(signInState)}
            </div>
        </InputLabelContext.Provider>
    )
}
