import React from "react";

export function FindChannelsErrorView(
    {error}:  {error: string}
): React.JSX.Element {
    return (
        <div>
            <h1>FindChannelsErrorView</h1>
            <div>{error}</div>
        </div>
    )
}