import {useEffect, useReducer} from "react";

/**
 * The response from the fetch.
 *
 * @type State
 *
 * @prop body The body of the request.
 * @prop method The method of the request.
 * @prop data The data from the response.
 * @prop isLoading The loading state of the request.
 * @prop error The error from the response.
 */
type State = {
    body: { [key: string]: string }
    method: "GET" | "POST" | "PUT" | "DELETE"
    isLoading: boolean,
}

/**
 * The action to be dispatched.
 *
 * @type Action
 *
 * @prop type The type of the action.
 * @prop data The data from the response.
 * @prop error The error from the response.
 * @prop key The key of the body.
 * @prop value The value of the body.
 * @prop method The method of the request.
 */
type Action =
    { type: "done" } |
    { type: "update", key: string, value: string } |
    { type: "reset" } |
    { type: "submit" }

/**
 * Update the body of the request.
 *
 * @param body
 * @param key
 * @param value
 *
 * @returns The new body.
 */
function updateBody(
    body: { [key: string]: string },
    key: string,
    value: string,
): { [key: string]: string } {
    let newBody: { [key: string]: string } = {[key]: value}
    for (let k in body) {
        if (k !== key) {
            newBody[k] = body[k]
        }
    }
    return newBody
}

/**
 * The reducer for the fetch.
 *
 * @param state The state of the fetch.
 * @param action The action to be dispatched.
 *
 * @returns The new state.
 */
function reduce(state: State, action: Action): State {
    switch (action.type) {
        case "update":
            return {...state, body: updateBody(state.body, action.key, action.value)}
        case "reset":
            return {...state, body: {}, isLoading: false}
        case "submit":
            return {...state, isLoading: true}
        case "done":
            return {...state, isLoading: false}
    }
}

/**
 * The fetch handler.
 *
 * @type FetchHandler
 */
type FetchHandler = {
    /**
     * Fetch the data.
     */
    toFetch: () => void
    /**
     * Update the body.
     *
     * @param key The key of the body.
     * @param value The value of the body.
     */
    toUpdate: (key: string, value: string) => void
    /**
     * Reset the body.
     */
    toReset: () => void
}

/**
 * The fetch hook.
 *
 * @param url The url of the fetch
 * @param method The method of the fetch
 * @param body The body of the fetch
 * @param onSuccess The success handler
 * @param onError The error handler
 * @returns The state and the fetch handler.
 */
export function useFetch(
    url: string,
    method: "GET" | "POST" | "PUT" | "DELETE" = "GET",
    onSuccess: (response: Response) => void = () => {},
    onError: (error: Error) => void = () => {},
    body: { [key: string]: string } = {},
): FetchHandler {
    const [state, dispatch] =
        useReducer(
            reduce,
            {
                body,
                method,
                isLoading: false,
            }
        )
    useEffect(() => {
        if (state.isLoading) {
            const controller = new AbortController()
            fetch(url, {
                method: state.method,
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(state.body),
                signal: controller.signal,
                credentials: "include"
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error(response.statusText)
                    }
                    return response
                })
                .then(response => {
                    onSuccess(response)
                })
                .catch(error => {
                    onError(error)
                })
            return () => {
                controller.abort("Fetch aborted due to component unmount")
            }
        }
    }, [url, state.isLoading])
    const toFetch = () => {
        dispatch({type: "submit"})
    }
    const toUpdate = (key: string, value: string) => dispatch({type: "update", key, value})
    const toReset = () => dispatch({type: "reset"})
    return {toFetch, toUpdate, toReset}
}