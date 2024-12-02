/**
 * The ChannelHandler type is a collection of functions
 * that can be used to interact with the channel state.
 *
 * @method loadChannels - Load the initial set of channels
 * @method loadMore - Load more channels
 * @method search - Search for channels
 * @method clear - Clear the search results
 * @method goBack - Go back to the previous state
 */
export type ChannelsHandler = {
    loadChannels: () => void
    loadMore: (offset:number, at: "head" | "tail") => void
    search: (query: string) => void
    searchMore: (query: string, offset: number, at: "head" | "tail") => void
    clear: () => void
    goBack: () => void
}