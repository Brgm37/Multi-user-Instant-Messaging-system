import {Context, createContext} from "react";
import {Either} from "../../model/Either";
import {Channel} from "../../model/Channel";

export interface EditChannelServiceContext {
    editChannel(
        id: string,
        description?: string,
        visibility?: string,
        icon?: string,
    ): Promise<Either<void, string>>

    loadChannel(id: string): Promise<Either<Channel, string>>
}

const defaultEditChannelServiceContext: EditChannelServiceContext = {
    editChannel: (): Promise<Either<void, string>> => {
        throw new Error("Not implemented")
    },
    loadChannel: (): Promise<Either<Channel, string>> => {
        throw new Error("Not implemented")
    }
}

export const EditChannelServiceContext: Context<EditChannelServiceContext> =
    createContext(defaultEditChannelServiceContext)