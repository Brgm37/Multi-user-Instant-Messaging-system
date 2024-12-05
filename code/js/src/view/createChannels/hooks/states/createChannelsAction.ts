import {ChannelInput} from "./createChannelsState";

export type CreateChannelsAction =
    { type: "edit", input: "name" | "visibility" | "access", inputValue: string} |
    { type: "error", message: string} |
    { type: "success", input: ChannelInput } |
    { type: "submit" }