import * as React from "react";
import {ChangeEvent, Context} from "react";

/**
 * The props for the InputElement component.
 *
 * @param value The input value.
 * @param onChange The input change handler.
 * @param error The input error message.
 * @param validate The input validation function.
 */
type InputElementProps = {
    value: string,
    onChange: (event: ChangeEvent<HTMLInputElement>) => void,
    error: string,
    validate: (value: string) => void,
}
/**
 * The context for the InputLabel component.
 *
 * This context is used to store the input values of the InputLabel component.
 *
 * @param username The username input value.
 * @param password The password input value.
 * @param confirmPassword The confirm password input value.
 * @param invitationCode The invitation code input value.
 */
export interface InputFormContext {
    username: InputElementProps,
    password: InputElementProps,
    confirmPassword?: InputElementProps,
    invitationCode?: InputElementProps,
}

/**
 * The default context for the InputLabel component.
 */
const defaultInputFormContext: InputFormContext = {
    username: {
        value: "",
        onChange: () => {throw Error("onChange not implemented")},
        error: "",
        validate: () => {throw Error("validate not implemented")},
    },
    password: {
        value: "",
        onChange: () => {throw Error("onChange not implemented")},
        error: "",
        validate: () => {throw Error("validate not implemented")},
    },
}

/**
 * The context for the InputLabel component.
 */
export const InputFormContext: Context<InputFormContext> =
    React.createContext<InputFormContext>(defaultInputFormContext)