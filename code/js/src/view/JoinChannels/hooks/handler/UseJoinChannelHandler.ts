
export type UseJoinChannelHandler = {
    /**
     * Join a channel.
     * @param joinCode
     */
    onJoin: (joinCode: string) => void
}