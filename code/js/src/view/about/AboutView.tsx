import * as React from 'react'
import {DevView} from "./components/DevView";
import {Dev} from "./hooks/states/AboutState";

export function AboutView(): React.JSX.Element  {
    return (
        <div>
            <h1>About Developers</h1>
            {Developers.map((dev) => (
                <DevView dev={dev} />
            ))}
        </div>
    )
}

const Developers: Dev[] =
    [
        {
            name: "Arthur Oliveira",
            num: 50543,
            bio: "I am a developer",
            email: "A50543@alunos.isel.pt",
            github: new URL("https://github.com/Thuzys"),
            linkdIn: new URL("https://www.linkedin.com/in/arthur-cesar-oliveira-681643184/"),
            imageURL: "https://avatars.githubusercontent.com/u/114019837?v=4"
        },
        {
            name: "Brian Melhorado",
            num: 50471,
            bio: null,
            email: "A50471@alunos.isel.pt",
            github: new URL("https://github.com/Brgm37"),
            linkdIn: new URL("https://www.linkedin.com/in/brian-melhorado-449794307/"),
            imageURL: "https://avatars.githubusercontent.com/u/114441596?v=4"
        },
        {
            name: "Mariana Moraes",
            num: 50560,
            bio: null,
            email: "A50560@alunos.isel.pt",
            github: new URL("https://github.com/mariana-moraess"),
            linkdIn: new URL("https://www.linkedin.com/in/mariana-moraes-92975b330/"),
            imageURL: "https://avatars.githubusercontent.com/u/146859944?s=400&v=4"
        }
    ]