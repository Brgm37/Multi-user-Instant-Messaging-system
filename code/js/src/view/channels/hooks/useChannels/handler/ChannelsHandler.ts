/**
 * The handler for the channels.
 *
 * The handler is used to interact with the channels.
 * It provides methods to load the initial set of channels,
 * load more channels, and go back to the previous state.
 *
 * @method loadChannels - Load the initial set of channels
 * @method loadMore - Load more channels
 * @method goBack - Go back to the previous state
 */
export type ChannelsHandler = {
    loadChannels: () => void
    loadMore: (offset:number, at: "head" | "tail") => void
    goBack: () => void
}