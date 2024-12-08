
/**
 * The handler for the FindChannels component.
 *
 * The handler is used to interact with the FindChannels component.
 * It provides methods to initialize the component, handle search changes,
 * join a channel, close the error message, and fetch more channels.
 *
 * @method onInit - The function to call when the component is initialized.
 * @method onSearchChange - The function to call when the search bar changes.
 * @method onJoin - The function to call when the user wants to join a channel.
 * @method onErrorClose - The function to call when the user wants to close the error message.
 * @method onFetchMore - The function to call when the user wants to fetch more channels.
 */
export type UseFindChannelsHandler = {
    onInit: () => void
    onSearchChange: (searchBar: string) => void
    onJoin: (channelId: number) => void
    onErrorClose: () => void
    onFetchMore: (offset: number, at: "head" | "tail") => void
}