import {ChannelInput} from "./createChannelsState";
import {UNSAFE_AssetsManifest} from "react-router-dom";

export type CreateChannelsAction =
    { type: "editName", inputValue: string} |
    { type: "editDescription", inputValue: string} |
    { type: "error", message: string} |
    { type: "success", input: ChannelInput } |
    { type: "submit" } |
    { type: "go-back" } |
    { type: "validation", name: string} |
    { type: "validated", isValidInput: boolean }
