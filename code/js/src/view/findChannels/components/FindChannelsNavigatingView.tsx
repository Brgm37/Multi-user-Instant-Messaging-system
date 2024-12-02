import React from "react";
import {PublicChannel} from "../model/PublicChannel";
import {PublicChannelsList} from "./shared/PublicChannelsList";

export function FindChannelsNavigatingView(
    {channels, onClick}: {channels: PublicChannel[], onClick: (channelId: number) => void}
): React.JSX.Element {
    return (
        <div>
            <section className={"p-8"}>
                <PublicChannelsList channels={channels} onClick={onClick}/>
            </section>
        </div>
    )
}