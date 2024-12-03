export type UseChannelHandler = {
    initChannel(): void
    loadMore(at: "head" | "tail"): void
    sendMsg(msg: string): void
}