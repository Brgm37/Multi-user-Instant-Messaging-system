import * as React from "react";

/**
 * The channel communication context.
 *
 * @interface ChannelCommunicationContext
 * @property {boolean} isToReload - The flag to reload the channel.
 * @property {() => void} toggleReload - The function to toggle the reload flag.
 */
export interface ChannelCommunicationContext {
    isToReload: boolean;
    toggleReload: () => void;
}

const defaultContext: ChannelCommunicationContext = {
    isToReload: false,
    toggleReload: () => { throw Error("Not implemented") },
}

/**
 * The channel communication context.
 */
export const ChannelCommunicationContext = React.createContext(defaultContext);
