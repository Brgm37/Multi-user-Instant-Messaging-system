
export type Dev = {
    name: string,
    num: number,
    bio: string | null,
    email: string,
    github: URL,
    linkdIn: URL,
    imageURL: string,
}

export type AboutState =
    { tag: "iddle" } |
    { tag: "devView", dev: Dev } |
    { tag: "redirect" }
