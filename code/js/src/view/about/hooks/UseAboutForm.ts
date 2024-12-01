import React from "react";
import {AboutState, Dev, makeInitialState} from "./states/AboutState";
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
                case "toggleExpandBio":
                    return {tag: "expandedBio", dev: state.dev}
                case "selectDev": {
                    const dev = {...action.dev}
                    return {tag: "devView", dev}
                }
                default:
                    throw Error("Invalid action")
            }
        case "expandedBio":
            switch (action.type) {
                case "acessGithub":
                    return {tag: "redirect"}
                case "acessLinkedIn":
                    return {tag: "redirect"}
                case "toggleExpandBio":
                    return {tag: "devView", dev: state.dev}
                case "selectDev": {
                    const dev = {...action.dev}
                    return {tag: "devView", dev}
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



export function useAboutService(): [AboutState, UseAboutFormHandler] {
    const [state, dispatch] = React.useReducer(reduce, makeInitialState())

    const onSelectDev = (dev: Dev) => {
        dispatch({type: "selectDev", dev: dev})
    }

    const onToggleExpandBio = (dev: Dev) => {
        dispatch({type: "toggleExpandBio", dev: dev})
    }

    const onAcessGithub = () => {
        dispatch({type: "acessGithub"})
    }

    const onAcessLinkedIn = () => {
        dispatch({type: "acessLinkedIn"})
    }

    return [state, {onSelectDev, onToggleExpandBio, onAcessGithub, onAcessLinkedIn}]
}