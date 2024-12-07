import React from "react";
import {expirationDateToTimeFormat} from "../../../service/utils/expirationDateToTimeFormat";

const expirationDateOptions = [
    "30 minutes",
    "1 hour",
    "6 hours",
    "1 day",
    "7 days"
]

export function CreateUserInvitationEditingView(
    {onGenerate}: {onGenerate: (expirationDate: string) => void}
): React.JSX.Element {
    const handleGenerateCode = () => {
        const expirationDate = expirationDateToTimeFormat((document.querySelector("select[title=expirationDate]") as HTMLSelectElement).value);
        onGenerate(expirationDate);
    }

    return (
        <div>
            <div className="mb-4">
                <label className="block text-sm font-medium mb-1">EXPIRE AFTER</label>
                <select title={"expirationDate"} className= "bg-gray-700 text-white p-2 rounded w-full">
                    {expirationDateOptions.map((option) => (
                            <option key={option} value={option}>{option}</option>
                    ))}
                </select>
            </div>
            <div className="mb-4">
                <div className="flex flex-col">
                    <span className="text-sm text-gray-400">
                        Warning: Notice that previous invitations created will be deleted.
                    </span>
                </div>
            </div>
        <div className="flex justify-between">
            <button type={"submit"} onClick={handleGenerateCode} className="bg-blue-600 hover:bg-blue-700 text-white py-2 px-4 rounded ml-auto">Generate a New Invitation Code</button>
        </div>
        </div>
)
}