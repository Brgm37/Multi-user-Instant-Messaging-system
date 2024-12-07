import React from "react";
import {AccessControl} from "../../../model/AccessControl";
import {UseCreateChannelInvitationHandler} from "../hooks/handler/UseCreateChannelInvitationHandler";

/**
 * list of options for the expiration date of the invitation code
 */
const expirationDateOptions = [
    "30 minutes",
    "1 hour",
    "6 hours",
    "1 day",
    "7 days"
]

/**
 * list of options for the permissions of the invitation code
 */
const permissionsOptions = [
    "READ_WRITE",
    "READ_ONLY"
]

/**
 * list of options for the maximum number of uses of the invitation code
 */
const maxUsesOptions = [
    "1",
    "2",
    "3",
    "4",
    "5",
    "10",
    "25",
    "50",
    "100"
]

function expirationDateToTimeFormat(expirationDate: string): string {
    switch (expirationDate) {
        case "30 minutes":
            return "00:30:00";
        case "1 hour":
            return "01:00:00";
        case "6 hours":
            return "06:00:00";
        case "1 day":
            return "24:00:00";
        case "7 days":
            return "168:00:00";
        default:
            throw new Error("Invalid expiration date");
    }
}

export function CreateChannelInvitationEditingView({handler}: {handler: UseCreateChannelInvitationHandler}): React.ReactElement {
    const handleGenerateCode = () => {
        const expirationDate = expirationDateToTimeFormat((document.querySelector("select[title=expirationDate]") as HTMLSelectElement).value);
        const maxUses = (document.querySelector("select[title=maxUses]") as HTMLSelectElement).value;
        const accessControl = (document.querySelector("select[title=permissions]") as HTMLSelectElement).value as AccessControl;
        handler.onCreate(expirationDate, maxUses, accessControl);
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
                <label className="block text-sm font-medium mb-1">MAX NUMBER OF USES</label>
                <select title="maxUses" className="bg-gray-700 text-white p-2 rounded w-full">
                    {maxUsesOptions.map((option) => (
                        <option key={option} value={option}>{option}</option>
                    ))}
                </select>
            </div>
            <div className="mb-4">
                <label className="block text-sm font-medium mb-1">PERMISSIONS</label>
                <select title="permissions" className="bg-gray-700 text-white p-2 rounded w-full">
                    {permissionsOptions.map((option) => (
                        <option key={option} value={option}>{option}</option>
                    ))}
                </select>
            </div>
            <div className="mb-4">
                <div className="flex flex-col">
                    <span className="text-sm text-gray-400">
                        Warning: Notice that previous invitations created will be deleted.
                    </span>
                    <br/>
                    <span className="text-sm text-gray-400">
                        Suggestion: If you want to invite multiple users, increment maxUses instead of creating multiple invitation codes
                    </span>
                </div>
            </div>
            <div className="flex justify-between">
                <button type={"submit"} onClick={handleGenerateCode} className="bg-blue-600 hover:bg-blue-700 text-white py-2 px-4 rounded ml-auto">Generate a New Invitation Code</button>
            </div>
        </div>
    )
}