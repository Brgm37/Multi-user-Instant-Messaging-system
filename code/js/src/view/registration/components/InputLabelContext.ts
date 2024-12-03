import * as React from "react";
import {Context} from "react";

/**
 * The context for the InputLabel component.
 *
 * @prop value The value of the input field.
 * @prop onChange The function to handle the change event.
 * @prop error The error message for the input field.
 */
export interface InputLabelContext {
    input: {
        username: string,
        password: string,
        confirmPassword?: string,
        invitationCode?: string,
        isValid: boolean
    },
    error?: {
        usernameError?: string,
        passwordError?: string,
        confirmPasswordError?: string,
        invitationCodeError?: string
    }
}

/**
 * The default context for the InputLabel component.
 */
const defaultInputLabelContext: InputLabelContext = {
    input: {username: '', password: '', isValid: false},
}

/**
 * The context for the InputLabel component.
 */
export const InputLabelContext: Context<InputLabelContext> =
    React
        .createContext<InputLabelContext>(defaultInputLabelContext)