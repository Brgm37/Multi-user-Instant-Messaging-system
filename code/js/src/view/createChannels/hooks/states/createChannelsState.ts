
export type Visibility = "public" | "private"

export type Access = "READ_ONLY" | "READ_WRITE"

export type ChannelInput = {
    name: string,
    visibility: Visibility,
    access: Access,
    isValid: boolean
}

export type CreateChannelsState =
    { tag: "editing", input: ChannelInput} |
    { tag: "error", message: string, input: ChannelInput} |
    { tag: "submitting", input: ChannelInput } |
    { tag: "redirecting", input: ChannelInput }


export function makeInitialState(): CreateChannelsState {
    return {
        tag: "editing",
        input: {
            name: "",
            visibility: "public",
            access: "READ_ONLY",
            isValid: false,
        },

    }
}