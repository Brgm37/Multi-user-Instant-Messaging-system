import React from "react";
import {AboutState, Dev} from "./states/AboutState";
import {AboutAction} from "./states/AboutAction";
import {UseAboutFormHandler} from "./handler/UseAboutFormHandler";

function reduce(state: AboutState, action: AboutAction): AboutState {
    switch (state.tag) {
        case "iddle":
            switch (action.type) {
                case "selectDev": {
                    const dev = {...action.dev}
                    return {tag: "devView", dev}
                }
                default:
                    throw Error("Invalid action")
            }
        case "devView":
            switch (action.type) {
                case "acessGithub":
                    return {tag: "redirect"}
                case "acessLinkedIn":
                    return {tag: "redirect"}
                case "selectDev": {
                    return {tag: "iddle"}
                }
                default:
                    throw Error("Invalid action")
            }
        case "redirect":
            throw Error("Already in final State 'redirect' and should not reduce to any other State.")
        default:
            throw Error("Invalid state")
    }
}

const initialState: AboutState = {tag: "iddle"}

export function useAboutService(): [AboutState, UseAboutFormHandler] {
    const [state, dispatch] = React.useReducer(reduce, initialState)

    const onSelectDev = (dev: Dev) => {
        dispatch({type: "selectDev", dev: dev})
    }

    const onAcessGithub = () => {
        dispatch({type: "acessGithub"})
    }

    const onAcessLinkedIn = () => {
        dispatch({type: "acessLinkedIn"})
    }

    return [state, {onSelectDev, onAcessGithub, onAcessLinkedIn}]
}