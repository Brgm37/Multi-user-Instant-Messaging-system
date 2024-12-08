import {AccessControl} from "../../../../model/AccessControl";

export type Visibility = "public" | "private"

export type ChannelInput = {
    name: string,
    visibility: Visibility,
    access: AccessControl,
    isValid?: boolean,
    description?: string,
    icon?: string
}

export type CreateChannelsState =
    { tag: "editing", input: ChannelInput, message?: string} |
    { tag: "error", message: string, input: ChannelInput} |
    { tag: "submitting", input: ChannelInput } |
    { tag: "redirecting", input: ChannelInput } |
    { tag: "validating", input: ChannelInput }

export function makeInitialState(): CreateChannelsState {
    return {
        tag: "editing",
        input: {
            name: "",
            visibility: "public",
            access: "READ_ONLY",
        },
    }
}