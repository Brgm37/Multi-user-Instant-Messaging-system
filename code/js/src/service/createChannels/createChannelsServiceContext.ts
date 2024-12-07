import {Either} from "../../model/Either";
import {Channel} from "../../model/Channel";
import {Context, createContext} from "react";

export interface CreateChannelsServiceContext {
    createChannel(
        name: string,
        visibility: string,
        accessControl: string,
        description?: string,
        icon?: string
    ): Promise<Either<Channel, string>>

    findChannelByName(
        name: string,
    ): Promise<Either<Channel, string>>
}

const defaultCreateChannelsServiceContext: CreateChannelsServiceContext = {
    createChannel: (name, visibility, accessControl, description, icon) => {
        return new Promise<Either<Channel, string>>((_, reject) => {
            reject(new Error("Not implemented"))
        })
    },
    findChannelByName: (name) => {
        return new Promise<Either<Channel, string>>((_, reject) => {
            reject(new Error("Not implemented"))
        })
    }
}

export const CreateChannelsServiceContext: Context<CreateChannelsServiceContext> =
    createContext(defaultCreateChannelsServiceContext)