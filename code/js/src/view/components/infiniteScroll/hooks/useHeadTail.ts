import {useReducer} from "react";

/**
 * The state of the useHeadTail hook.
 */
type HeadTailState = {
    head: number
    tail: number
}
/**
 * The action of the useHeadTail hook.
 */
type HeadTailAction = {
    type: "head" | "tail"
    offset: number
}

/**
 * The reducer of the useHeadTail hook.
 * @param state
 * @param action
 */
function reduce(state: HeadTailState, action: HeadTailAction): HeadTailState {
    switch (action.type) {
        case "head":
            return {head: state.head + action.offset, tail: state.tail}
        case "tail":
            return {head: state.head, tail: state.tail + action.offset}
    }
}

/**
 * The handler of the useHeadTail hook.
 */
type HeadTailHandler = { setOffset: (type: "head" | "tail", offset: number) => void }

/**
 * The useHeadTail hook.
 */
export function useHeadTail(): [HeadTailState, HeadTailHandler] {
    const [state, dispatch] = useReducer(reduce, {head: 0, tail: 0})
    const setOffset = (type: "head" | "tail", offset: number) => dispatch({type, offset})
    return [state, {setOffset}]
}