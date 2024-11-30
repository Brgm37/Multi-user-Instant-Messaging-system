import {Either} from "../../../model/Either";
import {Channel} from "../../../view/findChannels/model/PublicChannel";
import {createContext} from "react";

export interface FindChannelsMockServiceContext {
    getChannelsByPartialName(partialName: string): Promise<Either<Channel[], string>>
    joinChannel(channelId: number): Promise<Either<void, string>>
    getPublicChannels(offset: number, limit: number): Promise<Either<Channel[], string>>
}

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

export const FindChannelsMockServiceContext =
    createContext<FindChannelsMockServiceContext>(defaultFindChannelsMockServiceContext)