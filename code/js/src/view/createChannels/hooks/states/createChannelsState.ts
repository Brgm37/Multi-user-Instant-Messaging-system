import {useSearchParams} from "react-router-dom";
import {extractSearchParams} from "../../../../service/utils/extractParams";

type Visibility = "public" | "private"

type PublicAccess = "READ_ONLY" | "READ_WRITE"

type ChannelInput = {
    name: string,
    visibility: Visibility,
    access: PublicAccess,
    isValid: boolean,
}

export type CreateChannelsState =
    { tag: "editing", input: ChannelInput, error?: string } |
    { tag: "error", message: string, input: ChannelInput } |
    { tag: "submitting", input: ChannelInput } |
    { tag: "redirecting" }


export function makeInitialState(): CreateChannelsState {
    const [searchParams] = useSearchParams()
    return {
        tag: "editing",
        input: {
            name: "",
            visibility: "public",
            access: "READ_ONLY",
            isValid: false
        }
    }
}