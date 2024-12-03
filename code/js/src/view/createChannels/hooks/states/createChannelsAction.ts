
export type CreateChannelsAction =
    { type: "edit", input: "name" | "visibility" | "access", inputValue: string} |
    { type: "error", error: string} |
    { type: "success" } |
    { type: "submit" } |
    { type: "validation-result", response: string | boolean }