import {Either} from "../../model/Either";
import {Context, createContext} from "react";
import {PublicChannel} from "../../model/PublicChannel";


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
     * @param offset
     * @param limit
     */
    getChannelsByPartialName(
        partialName: string,
        offset: number,
        limit: number,
    ): Promise<Either<PublicChannel[], string>>

    /**
     * Join channel method.
     * @param cId
     */
    joinChannel(
        cId: number,
    ): Promise<Either<void, string>>

    /**
     * Get public channels method.
     * @param offset
     * @param limit
     */
    getPublicChannels(
        offset: number,
        limit: number,
    ): Promise<Either<PublicChannel[], string>>
}

/**
 * Default FindChannels service context.
 */
const defaultFindChannelsServiceContext: FindChannelsServiceContext = {
    getChannelsByPartialName: () => {
        throw new Error("Not implemented")
    },
    joinChannel: () => {
        throw new Error("Not implemented")
    },
    getPublicChannels: () => {
        throw new Error("Not implemented")
    }
}

/**
 * FindChannels service context.
 */
export const FindChannelsServiceContext: Context<FindChannelsServiceContext> =
    createContext<FindChannelsServiceContext>(defaultFindChannelsServiceContext)