import * as React from "react";
import {ChannelsCommunicationContext} from "./ChannelsCommunicationContext";

export function ChannelsCommunicationProvider(props: { children: React.ReactNode }) {
    const [isToReload, setIsToReload] = React.useState(false);

    const toggleReload = () => {
        setIsToReload(!isToReload)
    }

    return (
        <ChannelsCommunicationContext.Provider value={{isToReload, toggleReload}}>
            {props.children}
        </ChannelsCommunicationContext.Provider>
    );
}