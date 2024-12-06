/**
 *  The handler for the channel.
 *
 *  The handler is used to interact with the channel.
 *  It provides methods to initialize the channel,
 *  load more messages, send a message, and go back to the previous state.
 *
 */
export type UseChannelHandler = {
    initChannel(): void
    loadMore(at: "head" | "tail"): void
    sendMsg(msg: string): void
    goBack(): void,
    reset(): void
}