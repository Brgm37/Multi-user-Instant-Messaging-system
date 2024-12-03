export type UseFindChannelsHandler = {
    /**
     * The function to call when the search bar changes.
     * @param searchBar
     * @returns void
     */
    onSearchChange: (searchBar: string) => void

    /**
     * The function to call when the user wants to join a channel.
     *
     * @param channelId The id of the channel to join.
     * @returns void
     */
    onJoin: (channelId: number) => void

    /**
     * The function to call when the user wants to close the error message.
     *
     * @returns void
     */
    onErrorClose: () => void

    /**
     * The function to call when the user wants to fetch more channels.
     *
     * @param offset The offset to fetch from.
     * @param limit The limit of channels to fetch.
     * @returns void
     */
    onFetchMore: () => void
}