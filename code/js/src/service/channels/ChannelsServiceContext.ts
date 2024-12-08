import {Either} from "../../model/Either";
import {Channel} from "../../model/Channel";
import {Context, createContext} from "react";

/**
 * The context for the channels service.
 *
 * @method findChannels
 * @method findChannelsByName
 * @method logout
 */
export interface ChannelsServiceContext {
    /**
     * Find channels.
     *
     * @param offset
     * @param limit
     */
    findChannels(offset: number, limit: number): Promise<Either<Channel[], string>>

    /**
     * Find channels by name.
     *
     * @param name
     * @param offset
     * @param limit
     */
    findChannelsByName(name: string, offset: number, limit: number): Promise<Either<Channel[], string>>

    /**
     * Logout.
     */
    logout(): void
}

/**
 * The default channels service context.
 */
const defaultChannelsServiceContext: ChannelsServiceContext = {
    findChannels: () => {
        throw Error("Not implemented")
    },
    findChannelsByName: () => {
        throw Error("Not implemented")
    },
    logout: () => {
        throw Error("Not implemented")
    }
}

/**
 * The context for the channels service.
 */
export const ChannelsServiceContext: Context<ChannelsServiceContext> =
    createContext(defaultChannelsServiceContext)