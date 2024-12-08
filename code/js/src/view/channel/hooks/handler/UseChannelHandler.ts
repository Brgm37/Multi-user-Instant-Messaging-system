/**
 *  The handler for the channel.
 *
 *  The handler is used to interact with the channel.
 *  It provides methods to initialize the channel, load more messages,
 *  send a message, go back to the previous state, reset the channel,
 *  and handle an error.
 *
 *  @method initChannel Initializes the channel.
 *  @method loadMore Loads more messages.
 *  @method sendMsg Sends a message.
 *  @method goBack Goes back to the previous state.
 *  @method reset Resets the channel.
 *  @method error Handles an error.
 */
export type UseChannelHandler = {
    initChannel(): void
    loadMore(at: "head" | "tail"): void
    sendMsg(msg: string): void
    goBack(): void,
    reset(): void,
    error(error: string): void
}