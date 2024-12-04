import * as React from 'react';
import {Navigate, useLocation} from "react-router-dom";
import useLogin from "./hooks/UseLogin";
import {useInput} from "../components/input/useInput";
import {useEffect} from "react";
import {InputFormContext} from "../components/InputFormContext";
import {usernameValidation} from "../../../service/registration/validation/UsernameValidation";
import {LoginBaseView} from "./components/LoginBaseView";

export function LoginView(): React.JSX.Element {
    const location = useLocation();
    const [state, handler] = useLogin();
    const [username, usernameHandler] = useInput();
    const [password, passwordHandler] = useInput();

    useEffect(() => {
        if (username.error === "" && password.error === "") {
            handler.onIsValidChange(true);
        } else if (state.tag === "editing" && state.isValid) {
            handler.onIsValidChange(false);
        }
    }, [username, password]);

    if (state.tag === "redirect") {
        let source = location.state?.source;
        if (!source) source = "/channels";
        else location.state.source = undefined;
        return <Navigate to={source} replace={true} />;
    }

    const form: InputFormContext = {
        username: {
            value: username.value,
            onChange: (event: React.ChangeEvent<HTMLInputElement>) => {
                if (state.tag === "error") handler.goBack();
                usernameHandler.setValue(event.target.value);
            },
            error: username.error,
            validate: (value: string) => {
                const error = usernameValidation(value);
                usernameHandler.setError(error === true ? "" : error);
            }
        },
        password: {
            value: password.value,
            onChange: (event: React.ChangeEvent<HTMLInputElement>) => {
                if (state.tag === "error") handler.goBack();
                passwordHandler.setValue(event.target.value);
            },
            error: password.error,
            validate: (value: string) => {
                const error = usernameValidation(value);
                passwordHandler.setError(error === true ? "" : error);
            }
        },
    };

    return (
        <InputFormContext.Provider value={form}>
            <LoginBaseView
                inputsDisabled={state.tag === "submitting"}
                isValid={state.tag === "editing" && state.isValid}
                onSubmit={() => handler.onSubmit(username.value, password.value)}
                error={state.tag === "error" ? state.message : undefined}
            />
        </InputFormContext.Provider>
    );
}