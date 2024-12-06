import {Either} from "../../model/Either";
import {CreateChannel} from "../../view/createChannels/model/CreateChannel";
import {Channel} from "../../model/Channel";
import {Context, createContext} from "react";

export interface CreateChannelsServiceContext {
    createChannel(
        name: string,
        visibility: string,
        accessControl: string,
    ): Promise<Either<CreateChannel, string>>

    findChannelByName(
        name: string,
    ): Promise<Either<Channel, string>>
}

const defaultCreateChannelsServiceContext: CreateChannelsServiceContext = {
    createChannel: () => {
        return new Promise<Either<CreateChannel, string>>((_, reject) => {
            reject(new Error("Not implemented"))
        })
    },
    findChannelByName: () => {
        return new Promise<Either<Channel, string>>((_, reject) => {
            reject(new Error("Not implemented"))
        })
    }
}

export const CreateChannelsServiceContext: Context<CreateChannelsServiceContext> =
    createContext(defaultCreateChannelsServiceContext)