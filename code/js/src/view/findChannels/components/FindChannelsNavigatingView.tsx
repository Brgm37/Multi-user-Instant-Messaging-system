import React from "react";
import {PublicChannel} from "../model/PublicChannel";

export function FindChannelsNavigatingView(
    {channels}: {channels: PublicChannel[]}
): React.JSX.Element {
    return (
        <div>
            <h1>FindChannelsNavigatingView</h1>
            <ul>
                {channels.map((channel) => (
                    <li key={channel.id}>{channel.name}</li>
                ))}
            </ul>
        </div>
    )
}