import * as React from "react";
import { ChannelCommunicationContext } from "./ChannelCommunicationContext";

/**
 * The channel communication provider.
 *
 * @param {React.ReactNode} children - The children.
 */
export function ChannelCommunicationProvider({ children }: { children: React.ReactNode }) {
    const [isToReload, setIsToReload] = React.useState(false);

    const toggleReload = () => {setIsToReload(!isToReload)}

    return (
        <ChannelCommunicationContext.Provider value={{ isToReload, toggleReload }}>
            {children}
        </ChannelCommunicationContext.Provider>
    );
}