import {Access, Visibility} from "../hooks/states/createChannelsState";
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
    {onGenerate}: {onGenerate: (visibility: Visibility, accessControl: Access) => void}
): React.JSX.Element {
    const handleGenerateCode = () => {
        const visibility = (document.querySelector("select[title=visibility]") as HTMLSelectElement).value as Visibility;
        const accessControl = (document.querySelector("select[title=accessControl]") as HTMLSelectElement).value as Access;
        onGenerate(visibility, accessControl);
    }
    return (
        <div>
            <div className="mb-4">
                <label className="block text-sm font-medium mb-1">
                    <br/>
                    CHOOSE CHANNEL VISIBILITY
                </label>
                <select title={"expirationDate"} className="bg-gray-700 text-white p-2 rounded w-full">
                    {visibilityOptions.map((option) => (
                        <option key={option} value={option}>{option}</option>
                    ))}
                </select>
                <label className="block text-sm font-medium mb-1">
                    <br/>
                    CHOOSE CHANNEL ACCESS CONTROL
                </label>
                <select title={"expirationDate"} className="bg-gray-700 text-white p-2 rounded w-full">
                    {accessControlOptions.map((option) => (
                        <option key={option} value={option}>{option}</option>
                    ))}
                </select>
                <span className="text-sm text-gray-400">
                        Warning: Notice that for private channels the access control does not matter.
                </span>
            </div>
        </div>
    )
}