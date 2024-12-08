import {useEffect, useReducer} from "react";

export type HasMore = {
    head: boolean,
    tail: boolean
}

export type UseScrollState<T> = {
    list: T[],
    max: number,
    hasMore: HasMore,
}

export type UseScrollHandler<T> = {
    addItems: (items: T[], type: "head" | "tail", hasMore: HasMore) => void
    reset: (items: T[], hasMore: HasMore) => void
}

type UseScrollAction<T> =
    { type: "addItems", items: T[], hasMore: HasMore } |
    { type: "reset", items: T[], hasMore: HasMore }

function reduce<T>(state: UseScrollState<T>, action: UseScrollAction<T>): UseScrollState<T> {
    switch (action.type) {
        case "addItems":
            return {...state, list: action.items, hasMore: action.hasMore}
        case "reset":
            return {...state, list: action.items, hasMore: action.hasMore}
        default:
            throw Error(`Unknown action: ${action}`)
    }
}

export default function <T>(
    maxSize: number,
): [UseScrollState<T>, UseScrollHandler<T>] {
    const initialState: UseScrollState<T> = {list: [], hasMore: {head: false, tail: false}, max: maxSize}
    const [state, dispatch] = useReducer(reduce<T>, initialState)
    const handler: UseScrollHandler<T> = {
        addItems: (items: T[], type: "head" | "tail", hasMore: HasMore) => {
            const currSize = state.list.length
            if (currSize + items.length >= maxSize) {
                const diff = currSize + items.length - maxSize
                if (type === "head") {
                    const cutList = state.list.slice(0, currSize - diff)
                    items = [...items, ...cutList]
                }
                else {
                    const cutList = state.list.slice(diff)
                    items = [...cutList, ...items]
                }
            } else {
                if (type === "head") items = [...items, ...state.list]
                else items = [...state.list, ...items]
            }
            dispatch({type: "addItems", items, hasMore})
        },
        reset: (items, hasMore) => {
            dispatch({type: "reset", items, hasMore})
        }
    }
    return [state, handler]
}
