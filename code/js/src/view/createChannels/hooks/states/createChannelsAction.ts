import {ChannelInput} from "./createChannelsState";

export type CreateChannelsAction =
    { type: "edit", inputValue: string} |
    { type: "error", message: string} |
    { type: "success", input: ChannelInput } |
    { type: "submit" } |
    { type: "go-back" }