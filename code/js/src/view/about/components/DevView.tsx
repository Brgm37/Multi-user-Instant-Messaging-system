import * as React from 'react'
import {Link} from "react-router-dom";
import { Dev } from '../../../service/about/AboutServices';

const dev: Dev = {
    name: "John Doe",
    bio: "I am a developer",
    email: "johndoe@gmail.com",
    github: new URL("https://github.com"),
    linkdIn: new URL("https://www.linkedin.com")
}

export function DevView(): React.JSX.Element  {
    return (
        <div>
            <h1>{dev.name}</h1>
            <p>{dev.bio}</p>
            <p>{dev.email}</p>
            <a href={dev.github.href}>Github</a>
            <p></p>
            <a href={dev.linkdIn.href}>LinkedIn</a>
        </div>
    )
}