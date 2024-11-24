import * as React from "react";
import {Context} from "react";

/**
 * The context for the InputLabel component.
 *
 * @prop value The value of the input field.
 * @prop onChange The function to handle the change event.
 * @prop error The error message for the input field.
 */
interface InputLabelContext {
    value: string
    onChange: (event: React.ChangeEvent<HTMLInputElement>) => void
    error: string
}

/**
 * The default context for the InputLabel component.
 */
const defaultInputLabelContext: InputLabelContext = {
    value: '',
    onChange: () => {
        throw Error('onChange not implemented')
    },
    error: undefined
}

/**
 * The context for the InputLabel component.
 */
export const InputLabelContext: Context<InputLabelContext> =
    React
        .createContext<InputLabelContext>(defaultInputLabelContext)