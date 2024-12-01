import React from "react"
import { urlBuilder } from "../utils/UrlBuilder"
import { useFetch } from "../utils/useFetch"

const aboutApiURL = urlBuilder("/about")

export type Dev = {
    name: string,
    bio: string | null,
    email: string,
    github: URL,
    linkdIn: URL
}

type AboutAction =  
    {type: "selectDev", dev: Dev} |
    {type: "toggleExpandBio"} |
    {type: "acessGithub"} |
    {type: "acessLinkedIn"}

type AboutState = 
    { tag: "iddle"} | 
    { tag: "devView", dev: Dev} |
    { tag: "expandedBio", dev: Dev} |
    { tag: "redirect"}

function makeInitialState(): AboutState {
    return { tag: "iddle" }
}

function reduce(state: AboutState, action: AboutAction): AboutState {
    switch (state.tag) {
        case "iddle":
            switch (action.type){
                case "selectDev": {
                    const dev = {...action.dev}
                    return {tag: "devView", dev}
                }
                default:
                    throw Error("Invalid action")    
            }
        case "devView":
            switch (action.type){
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
            switch (action.type){
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

export type UseAboutFormHandler = {
    onSelectDev: () => void,
    onToggleExpandBio: () => void,
    onAcessGithub: () => void,
    onAcessLinkedIn: () => void,
}

export function useAboutService(
): [AboutState, UseAboutFormHandler] {
    const [state, dispatch] = React.useReducer(reduce, makeInitialState())
    const fetchHandler = 
        useFetch(
            aboutApiURL,
            "GET",
            response => response.json().then((dev: Dev) => dispatch({type: "selectDev", dev})),
            response => console.log(response.message)
        )

    const onSelectDev = () => {
        fetchHandler.toFetch()
        dispatch({type: "selectDev", dev: {name: "", bio: "", email: "", github: new URL(""), linkdIn: new URL("")}})
    }

    const onToggleExpandBio = () => {
        dispatch({type: "toggleExpandBio"})
    }

    const onAcessGithub = () => {
        dispatch({type: "acessGithub"})
    }

    const onAcessLinkedIn = () => {
        dispatch({type: "acessLinkedIn"})
    }

    return [state, {onSelectDev, onToggleExpandBio, onAcessGithub, onAcessLinkedIn}]
}