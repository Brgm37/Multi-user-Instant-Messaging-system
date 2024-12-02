import {Either} from "../../model/Either";
import {Channel} from "../../model/Channel";
import {Context, createContext} from "react";

export interface ChannelsServiceContext {
    findChannels(offset: number, limit: number): Promise<Either<Channel[], string>>

    findChannelsByName(name: string, offset: number, limit: number): Promise<Either<Channel[], string>>
}

const defaultChannelsServiceContext: ChannelsServiceContext = {
    findChannels: () => {
        throw Error("Not implemented")
    },
    findChannelsByName: () => {
        throw Error("Not implemented")
    }
}

export const ChannelsServiceContext: Context<ChannelsServiceContext> =
    createContext(defaultChannelsServiceContext)