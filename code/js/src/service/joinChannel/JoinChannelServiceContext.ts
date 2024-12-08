import {Either} from "../../model/Either";
import {Context, createContext} from "react";

export interface JoinChannelServiceContext {
    /**
     * Join a channel.
     * @param invitationToken
     */
    joinChannel(
        invitationToken: string,
    ): Promise<Either<void, string>>
}

const defaultJoinChannelServiceContext: JoinChannelServiceContext = {
    joinChannel: (invitationToken) => {
        return new Promise<Either<void, string>>((resolve, reject) => {
            reject(new Error("Not implemented"))
        })
    }
}

export const JoinChannelServiceContext: Context<JoinChannelServiceContext> =
    createContext(defaultJoinChannelServiceContext)