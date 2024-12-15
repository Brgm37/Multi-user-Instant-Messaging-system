import {createContext} from "react";

/**
 * Context for communication between channels
 *
 * isToReload: boolean - flag to indicate if the channels should reload
 * toggleReload: () => void - function to toggle the reload flag
 */
export interface ChannelsCommunicationContext {
  isToReload: boolean;
  toggleReload: () => void;
}

const defaultContext: ChannelsCommunicationContext = {
    isToReload: false,
    toggleReload: () => {throw Error("toggleReload not implemented")}
}

/**
 * Context for communication between channels
 */
export const ChannelsCommunicationContext =
    createContext<ChannelsCommunicationContext>(defaultContext);