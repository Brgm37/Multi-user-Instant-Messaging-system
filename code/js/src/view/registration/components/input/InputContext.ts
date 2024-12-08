import {ChangeEvent, createContext} from "react";

/**
 * The context for the input.
 */
export interface InputContext {
    value: string;
    onChange: (event: ChangeEvent<HTMLInputElement>) => void;
    error: string | false;
    validate: (value: string) => void;
}

/**
 * The default context for the input.
 */
const defaultContext: InputContext = {
    onChange(): void {
        throw new Error("onChange not implemented")
    },
    validate(): void {
        throw new Error("validate not implemented")
    },
    value: "",
    error: false
};

/**
 * The context for the input.
 */
export const InputLabelContext = createContext(defaultContext);