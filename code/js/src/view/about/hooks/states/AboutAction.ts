import {Dev} from "./AboutState";


export type AboutAction =
    { type: "selectDev", dev: Dev } |
    { type: "toggleExpandBio", dev: Dev } |
    { type: "acessGithub" } |
    { type: "acessLinkedIn" }