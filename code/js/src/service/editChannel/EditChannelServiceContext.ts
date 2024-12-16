import {Context, createContext} from "react";
import {Either} from "../../model/Either";
import {Channel} from "../../model/Channel";
import {ChannelVisibility} from "../../model/ChannelVisibility";

/**
 * The context for the edit channel service.
 *
 * @method editChannel
 * @method loadChannel
 */
export interface EditChannelServiceContext {
    /**
     * Edit a channel.
     * @param id
     * @param description
     * @param visibility
     * @param icon
     */
    editChannel(
        id: string,
        description?: string,
        visibility?: ChannelVisibility,
        icon?: string,
    ): Promise<Either<void, string>>

    /**
     * Load a channel.
     * @param id
     */
    loadChannel(id: string): Promise<Either<Channel, string>>
}

/**
 * The default edit channel service context.
 */
const defaultEditChannelServiceContext: EditChannelServiceContext = {
    editChannel: (): Promise<Either<void, string>> => {
        throw new Error("Not implemented")
    },
    loadChannel: (): Promise<Either<Channel, string>> => {
        throw new Error("Not implemented")
    }
}

/**
 * The context for the edit channel service.
 */
export const EditChannelServiceContext: Context<EditChannelServiceContext> =
    createContext(defaultEditChannelServiceContext)