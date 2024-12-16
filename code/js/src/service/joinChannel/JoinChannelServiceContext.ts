import {Either} from "../../model/Either";
import {Context, createContext} from "react";

/**
 * The context for the join channel service.
 *
 * @method joinChannel
 */
export interface JoinChannelServiceContext {
    /**
     * Join a channel.
     * @param invitationToken
     */
    joinChannel(
        invitationToken: string,
    ): Promise<Either<{ id: string }, string>>
}

/**
 * The default join channel service context.
 */
const defaultJoinChannelServiceContext: JoinChannelServiceContext = {
    joinChannel: () => {
        throw new Error("Not implemented")
    }
}

/**
 * The context for the join channel service.
 */
export const JoinChannelServiceContext: Context<JoinChannelServiceContext> =
    createContext(defaultJoinChannelServiceContext)