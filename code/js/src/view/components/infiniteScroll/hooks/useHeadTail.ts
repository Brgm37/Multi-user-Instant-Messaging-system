import {useReducer} from "react";

type HeadTailState = {
    head: number
    tail: number
}

type HeadTailAction = {
    type: "head" | "tail"
    offset: number
}

function reduce(state: HeadTailState, action: HeadTailAction): HeadTailState {
    switch (action.type) {
        case "head":
            return {head: state.head + action.offset, tail: state.tail}
        case "tail":
            return {head: state.head, tail: state.tail + action.offset}
    }
}

type HeadTailHandler = { setOffset: (type: "head" | "tail", offset: number) => void }

export function useHeadTail(): [HeadTailState, HeadTailHandler] {
    const [state, dispatch] = useReducer(reduce, {head: 0, tail: 0})
    const setOffset = (type: "head" | "tail", offset: number) => dispatch({type, offset})
    return [state, {setOffset}]
}