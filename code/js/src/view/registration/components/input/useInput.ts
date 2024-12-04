import {useReducer} from "react";

type InputState = {
    value: string,
    error: string
}

type InputAction =
    {type: "change", value: string} |
    {type: "error", error: string}

function reducer(state: InputState, action: InputAction): InputState {
    switch (action.type) {
        case "change":
            return {value: action.value, error: state.error}
        case "error":
            return {value: state.value, error: action.error}
    }
}

export type InputHandler = {
    setValue: (value: string) => void,
    setError: (error: string) => void
}

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