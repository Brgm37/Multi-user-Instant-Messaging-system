import * as React from 'react';
import {Navigate, useLocation} from "react-router-dom";
import {InputFormContext} from '../components/InputFormContext';
import useRegister from "./hooks/UseRegister";
import {SignInBaseView} from "./components/SignInBaseView";
import {useInput} from "../components/input/useInput";
import {usernameValidation} from "../../../service/registration/validation/UsernameValidation";
import {passwordValidation} from "../../../service/registration/validation/PasswordValidation";
import {useEffect} from "react";
import {RegisterCommunicationContext} from "../../../service/registration/communication/RegisterCommunicationContext";

/**
 * The register view.
 */
export function RegisterView(): React.JSX.Element {
    const context = React.useContext(RegisterCommunicationContext);
    const [state, handler] = useRegister();
    const location = useLocation();

    const [username, usernameHandler] = useInput(context.username);
    const [password, passwordHandler] = useInput(context.password);
    const [confirmPassword, confirmPasswordHandler] = useInput();
    const [invitationCode, invitationCodeHandler] = useInput();

    useEffect(() => {
        if (
            username.error === "" &&
            password.error === "" &&
            confirmPassword.error === "" &&
            invitationCode.error === ""
        ) handler.setIsValid(true);
        else if (state.tag === "editing" && state.isValid) handler.setIsValid(false);
    }, [username, password, confirmPassword, invitationCode]);

    useEffect(() => {
        if (state.tag === "error") confirmPasswordHandler.setValue("");
    }, [state.tag]);

    if (state.tag === "redirect") {
        let source = location.state?.source;
        if (!source) source = "/channels/findChannels";
        else location.state.source = undefined;
        return <Navigate to={source} replace={true} />;
    }

    const form: InputFormContext = {
        username: {
            value: username.value,
            onChange: (event: React.ChangeEvent<HTMLInputElement>) => {
                if (state.tag === "error") handler.goBack();
                context.setUsername(event.target.value);
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
                context.setPassword(event.target.value);
                passwordHandler.setValue(event.target.value);
            },
            error: password.error,
            validate: (value: string) => {
                const error = passwordValidation(value);
                passwordHandler.setError(error === true ? "" : error);
            }
        },
        confirmPassword: {
            value: confirmPassword.value,
            onChange: (event: React.ChangeEvent<HTMLInputElement>) => {
                if (state.tag === "error") handler.goBack();
                confirmPasswordHandler.setValue(event.target.value);
            },
            error: confirmPassword.error,
            validate: (value: string) => {
                const error =
                    value === password.value || value.length === 0 ?
                        "" :
                        "Passwords do not match";
                confirmPasswordHandler.setError(error);
            }
        },
        invitationCode: {
            value: invitationCode.value,
            onChange: (event: React.ChangeEvent<HTMLInputElement>) => {
                if (state.tag === "error") handler.goBack();
                invitationCodeHandler.setValue(event.target.value);
            },
            error: invitationCode.error,
            validate: () => {
                // No validation for invitation code
            }
        }
    };

    return (
        <InputFormContext.Provider value={form}>
            <div>
                <SignInBaseView
                    inputsDisabled={state.tag === "submitting"}
                    isValid={state.tag === "editing" && state.isValid}
                    error={state.tag === "error" ? state.message : undefined}
                    onSubmit={() => handler.onSubmit(username.value, password.value, invitationCode.value)}
                />
            </div>
        </InputFormContext.Provider>
    );
}