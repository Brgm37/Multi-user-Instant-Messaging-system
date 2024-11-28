import * as React from "react"
import {useSearchParams} from "react-router-dom";

type Dev = {
    name: string,
    bio: string,
    email: string,
    github: URL,
    linkdIn: URL
}

type State = {
    devs: Dev[]
}

type Action =  
    {type: "selectDev"}
    {type: "toggleExpandBio"}
    {type: "acessGithub"}
    {type: "acessLinkedIn"}

type UseAboutFormHandler = {
    
}