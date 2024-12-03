import {Channel} from "../../model/Channel";
import {Message} from "../../model/Message";
import {Either} from "../../model/Either";
import {Context, createContext} from "react";

/**
 * The context for the channel service.
 *
 * @method loadChannel
 */
export interface ChannelServiceContext {
    /**
     * Load a channel by its id.
     *
     * @param cId
     *
     * @returns Channel | string
     */
    loadChannel(
        cId: string,
    ): Promise<Either<Channel, string>>

    /**
     * Load more messages for a channel.
     *
     * @param cId
     * @param timestamp
     * @param limit
     * @param at
     */
    loadMore(
        cId: string,
        timestamp: string,
        limit: number,
        at: "before" | "after",
    ): Promise<Either<Message[], string>>

    /**
     * Send a message to a channel.
     *
     * @param cId
     * @param msg
     */
    sendMsg(
        cId: string,
        msg: string,
    ): Promise<Either<Message, string>>
}

/**
 * The default channel service.
 */
const defaultChannelService: ChannelServiceContext = {
    async loadChannel(): Promise<Either<Channel, string>> {
        throw Error("Not implemented")
    },
    async loadMore(): Promise<Either<Message[], string>> {
        throw Error("Not implemented")
    },
    async sendMsg(): Promise<Either<Message, string>> {
        throw Error("Not implemented")
    },
}

/**
 * The context for the channel service.
 */
export const ChannelServiceContext:Context<ChannelServiceContext> =
    createContext<ChannelServiceContext>(defaultChannelService)