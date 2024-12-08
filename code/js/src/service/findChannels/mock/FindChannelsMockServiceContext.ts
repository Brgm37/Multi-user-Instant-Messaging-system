import {Either} from "../../../model/Either";
import {createContext} from "react";
import {Channel} from "../../../model/Channel";

/**
 * The context for the find channels mock service.
 *
 * @method getChannelsByPartialName
 * @method joinChannel
 * @method getPublicChannels
 */
export interface FindChannelsMockServiceContext {
    getChannelsByPartialName(partialName: string): Promise<Either<Channel[], string>>
    joinChannel(channelId: number): Promise<Either<void, string>>
    getPublicChannels(offset: number, limit: number): Promise<Either<Channel[], string>>
}

/**
 * The default find channels mock service context.
 */
const defaultFindChannelsMockServiceContext: FindChannelsMockServiceContext = {
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

/**
 * The context for the find channels mock service.
 */
export const FindChannelsMockServiceContext =
    createContext<FindChannelsMockServiceContext>(defaultFindChannelsMockServiceContext)