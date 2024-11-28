import * as React from 'react';
import { createContext, useState } from 'react';
import { PublicChannel } from '../../model/findChannels/PublicChannel';

/**
 * The context type for the public channels.
 * 
 * @type PublicChannelsContext 
 * @prop channels The list of public channels.
 * @prop setChannels The function to set the list of public channels.
 */
type PublicChannelsContextType = {
    channels: PublicChannel[]
    setChannels: (channels: PublicChannel[]) => void
}

/**
 * The context for the public channels.
 */
export const PublicChannelsContext = createContext<PublicChannelsContextType>({
    channels: [],
    setChannels: () => { throw new Error("Not implemented") }
})

/**
 * The provider for the public channels.
 * 
 * @param children The children of the provider.
 * @returns The provider for the public channels.
 */
export function PublicChannelsProvider({children}: {children: React.ReactNode}): React.JSX.Element {
    const [channels, setChannels] = useState<PublicChannel[]>([])
    return (
        <PublicChannelsContext.Provider value={{channels, setChannels}}>
            {children}
        </PublicChannelsContext.Provider>
    )
}
