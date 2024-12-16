import {Either} from "../../model/Either";
import {Channel} from "../../model/Channel";
import {Context, createContext} from "react";
import {AccessControl} from "../../model/AccessControl";
import {ChannelVisibility} from "../../model/ChannelVisibility";

/**
 * The context for the createChannels service.
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
        visibility: ChannelVisibility,
        accessControl: AccessControl,
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
        throw new Error("Not implemented")
    },
    findChannelByName: () => {
        throw new Error("Not implemented")
    }
}

/**
 * The context for the createChannels service.
 */
export const CreateChannelsServiceContext: Context<CreateChannelsServiceContext> =
    createContext(defaultCreateChannelsServiceContext)