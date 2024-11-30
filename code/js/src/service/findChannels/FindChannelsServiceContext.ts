import {Either} from "../../model/Either";
import {Channel} from "../../view/findChannels/model/PublicChannel";
import {Context, createContext} from "react";


/**
 * Interface for the FindChannels service.
 *
 * @method getChannelsByPartialName
 * @method joinChannel
 * @method getPublicChannels
 */
export interface FindChannelsServiceContext {

    /**
     * Get channels by partial name method.
     * @param partialName
     */
    getChannelsByPartialName(
        partialName: string,
    ): Promise<Either<Channel[], string>>

    /**
     * Join channel method.
     * @param channelId
     * @returns void
        */
    joinChannel(
        channelId: number,
    ): Promise<Either<void, string>>

    /**
     * Get public channels method.
     * @param offset
     * @param limit
     */
    getPublicChannels(
        offset: number,
        limit: number,
    ): Promise<Either<Channel[], string>>
}

const defaultFindChannelsServiceContext: FindChannelsServiceContext = {
    getChannelsByPartialName: (partialName) => {
        return new Promise<Either<Channel[], string>>((resolve, reject) => {
            reject(new Error("Not implemented"))
        })
    },
    joinChannel: (channelId) => {
        return new Promise<Either<void, string>>((resolve, reject) => {
            reject(new Error("Not implemented"))
        })
    },
    getPublicChannels: (offset, limit) => {
        return new Promise<Either<Channel[], string>>((resolve, reject) => {
            reject(new Error("Not implemented"))
        })
    }
}

export const FindChannelsServiceContext: Context<FindChannelsServiceContext> =
    createContext<FindChannelsServiceContext>(defaultFindChannelsServiceContext)