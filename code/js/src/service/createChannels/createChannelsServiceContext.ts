import {Either} from "../../model/Either";
import {Channel} from "../../model/Channel";
import {Context, createContext} from "react";

/**
 * The context for the create channels service.
 *
 * @method createChannel
 * @method findChannelByName
 */
export interface CreateChannelsServiceContext {
    /**
     * Create a channel.
     * @param name
     * @param visibility
     * @param accessControl
     * @param description
     * @param icon
     */
    createChannel(
        name: string,
        visibility: string,
        accessControl: string,
        description?: string,
        icon?: string
    ): Promise<Either<Channel, string>>

    /**
     * Find a channel by name.
     * @param name
     */
    findChannelByName(
        name: string,
    ): Promise<Either<Channel, string>>
}

/**
 * The default create channels service context.
 */
const defaultCreateChannelsServiceContext: CreateChannelsServiceContext = {
    createChannel: () => {
        return new Promise<Either<Channel, string>>((_, reject) => {
            reject(new Error("Not implemented"))
        })
    },
    findChannelByName: () => {
        return new Promise<Either<Channel, string>>((_, reject) => {
            reject(new Error("Not implemented"))
        })
    }
}

/**
 * The context for the create channels service.
 */
export const CreateChannelsServiceContext: Context<CreateChannelsServiceContext> =
    createContext(defaultCreateChannelsServiceContext)