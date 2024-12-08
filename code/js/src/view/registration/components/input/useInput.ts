import {useReducer} from "react";

/**
 * The state of an input field.
 */
type InputState = {
    value: string,
    error: string
}

/**
 * The action of an input field.
 */
type InputAction =
    {type: "change", value: string} |
    {type: "error", error: string}

/**
 * The reducer for the input field.
 * @param state
 * @param action
 */
function reducer(state: InputState, action: InputAction): InputState {
    switch (action.type) {
        case "change":
            return {value: action.value, error: state.error}
        case "error":
            return {value: state.value, error: action.error}
    }
}

/**
 * The handler for the input field.
 */
export type InputHandler = {
    setValue: (value: string) => void,
    setError: (error: string) => void
}

/**
 * The hook for an input field.
 * @param initialValue
 */
export function useInput(initialValue: string = ""): [InputState, InputHandler] {
    const [state, dispatch] = useReducer(reducer, {value: initialValue, error: ""})
    return [
        state,
        {
            setValue: (value: string) => dispatch({type: "change", value}),
            setError: (error: string) => dispatch({type: "error", error})
        }
    ]
}