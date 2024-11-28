import * as React from 'react'
import {useLoginForm} from './hooks/UseLoginForm'
import {InputLabelContext} from "../components/InputLabelContext";
import {LoginEditingView} from "./components/LoginEditingView";
import {Link, Navigate, useLocation} from "react-router-dom";
import {LoginSubmittingView} from "./components/LoginSubmittingView";
import {LoginErrorView} from "./components/LoginErrorView";
import {LoginState} from "./hooks/states/LoginState";

export function LoginView(): React.JSX.Element {
    const location = useLocation()
    const [loginState, handler] = useLoginForm()
    if (loginState.tag === "redirect") {
        let source = location.state?.source
        if (!source) source = "/home"
        return <Navigate to={source} replace={true}></Navigate>
    }
    let visibility = {password: false}
    let error = {usernameError: "", passwordError: ""}
    if (loginState.tag == "editing") {
        visibility = {password: loginState.visibility}
        error = {usernameError: loginState.error.usernameError, passwordError: loginState.error.passwordError}
    }
    const form = {
        input: loginState.input,
        visibility,
        error,
    }
    const view = ((state: LoginState) => {
        switch (state.tag) {
            case "editing":
                return <LoginEditingView handler={handler}/>
            case "submitting":
                return <LoginSubmittingView/>
            case "error":
                return <LoginErrorView message={state.message} handler={handler}/>
        }
    })
    return (
        <InputLabelContext.Provider value={form}>
            <div>
                <h1>Login</h1>
                {view(loginState)}
                <Link to={
                    {
                        pathname:"/signIn",
                        search:`?username=${loginState.input.username}&password=${loginState.input.password}`
                    }
                }>Register</Link>
            </div>
        </InputLabelContext.Provider>
    )
}