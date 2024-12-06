import {Access, ChannelInput, CreateChannelsState, Visibility} from "../hooks/states/createChannelsState";
import React from "react";

const accessControlOptions = [
    "READ_ONLY",
    "READ_WRITE"
]

const visibilityOptions =[
    "public",
    "private"
]

export function ToggleCreateChannelsView(
    {state, onGenerate}: {state: CreateChannelsState,onGenerate: (channel: ChannelInput) => void}
): React.JSX.Element {
    const handleGenerateCode = () => {
        const visibility = (document.querySelector("select[title=visibility]") as HTMLSelectElement).value as Visibility;
        const accessControl = (document.querySelector("select[title=accessControl]") as HTMLSelectElement).value as Access;
        const channel: ChannelInput = {
            name: state.input.name,
            visibility: visibility,
            access: accessControl,
            isValid: state.input.isValid
        };
        onGenerate(channel);
    }
    return (
        <div>
            <div className="mb-4">
                <label className="block text-sm font-medium mb-1">
                    <br/>
                    CHOOSE CHANNEL VISIBILITY
                </label>
                <select title={"visibility"} className="bg-gray-700 text-white p-2 rounded w-full">
                    {visibilityOptions.map((option) => (
                        <option key={option} value={option}>{option}</option>
                    ))}
                </select>
                <label className="block text-sm font-medium mb-1">
                    <br/>
                    CHOOSE CHANNEL ACCESS CONTROL
                </label>
                <select title={"accessControl"} className="bg-gray-700 text-white p-2 rounded w-full">
                    {accessControlOptions.map((option) => (
                        <option key={option} value={option}>{option}</option>
                    ))}
                </select>
                <span className="text-sm text-gray-400">
                        Warning: Notice that for private channels it will only serve as a default option.
                </span>
            </div>
            <div className="flex justify-between">
                <button
                    type={"submit"}
                    onClick={handleGenerateCode}
                    className="bg-blue-600 hover:bg-blue-700 text-white py-2 px-4 rounded ml-auto"
                >
                    Create Channel
                </button>
            </div>
        </div>
    )
}