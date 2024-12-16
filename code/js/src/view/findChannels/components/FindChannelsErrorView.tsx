import React from "react";

/**
 * The view for displaying an error when finding channels.
 *
 * @param error
 * @param onClose
 */
export function FindChannelsErrorView(
    {error, onClose}:  {error: string | Error, onClose: () => void}
): React.JSX.Element {
    const errorMessage = typeof error === "string" ? error : error.message;
    return (
        <div>
            <h1>FindChannelsErrorView</h1>
            <div>{errorMessage}</div>
            <button onClick={onClose}>Close</button>
        </div>
    )
}