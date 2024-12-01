import * as React from "react";
import useChannels from "./hooks/UseChannels";

export function ChannelsView():React.JSX.Element {
    const [state, dispatch] = useChannels()
    return (
        <div></div>
    )
}