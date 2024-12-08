import * as React from 'react'
import {DevView} from "./components/DevView";

/**
 * The about us view.
 */
export function AboutView(): React.JSX.Element  {
    return (
        <div className="container mx-auto p-4">
            <h1 className="text-center text-2xl font-bold mb-8">About Us</h1>
            <div className="flex flex-wrap justify-center">
                {Developers.map((dev) => (
                    <DevView dev={dev} />
                ))}
            </div>
        </div>
    )
}

/**
 * The developer type.
 */
export type Dev = {
    name: string,
    num: number,
    email: string,
    github: URL,
    linkedIn: URL,
    imageURL: string,
}

/**
 * The developers.
 */
const Developers: Dev[] =
    [
        {
            name: "Arthur Oliveira",
            num: 50543,
            email: "A50543@alunos.isel.pt",
            github: new URL("https://github.com/Thuzys"),
            linkedIn: new URL("https://www.linkedin.com/in/arthur-cesar-oliveira-681643184/"),
            imageURL: "https://avatars.githubusercontent.com/u/114019837?v=4"
        },
        {
            name: "Brian Melhorado",
            num: 50471,
            email: "A50471@alunos.isel.pt",
            github: new URL("https://github.com/Brgm37"),
            linkedIn: new URL("https://www.linkedin.com/in/brian-melhorado-449794307/"),
            imageURL: "https://avatars.githubusercontent.com/u/114441596?v=4"
        },
        {
            name: "Mariana Moraes",
            num: 50560,
            email: "A50560@alunos.isel.pt",
            github: new URL("https://github.com/mariana-moraess"),
            linkedIn: new URL("https://www.linkedin.com/in/mariana-moraes-92975b330/"),
            imageURL: "https://avatars.githubusercontent.com/u/146859944?s=400&v=4"
        }
    ]