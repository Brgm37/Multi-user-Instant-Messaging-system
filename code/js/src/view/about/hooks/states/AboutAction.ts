import {Dev} from "./AboutState";


export type AboutAction =
    { type: "selectDev", dev: Dev } |
    { type: "acessGithub" } |
    { type: "acessLinkedIn" }