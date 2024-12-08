import * as React from "react";
import {useContext, useEffect} from "react";
import {InputLabelContext} from "./InputContext";
import {InputLabel} from "../InputLabel";

/**
 * The timeout for the validation of the input.
 */
const TIMEOUT = 500

/**
 * The input for the username.
 *
 *
 * @param initialValue The initial value of the input.
 * @param isDisabled The disabled state of the input.
 */
export default function (
    {isDisabled, labelName, type}: { isDisabled: boolean, labelName: string, type: "text" | "password" }
): React.JSX.Element {
    const {error, validate, value, onChange} = useContext(InputLabelContext)
    const input = {
        value,
        onChange,
        error: error !== false ? error : ""
    }
    useEffect(() => {
        const timer = setTimeout(() => {
            validate(value)
        }, TIMEOUT)
        return () => clearTimeout(timer)
    }, [value]);
    return (
        <InputLabel
            label={labelName}
            type={type}
            disabled={isDisabled}
            input={input}
        />
    )
}