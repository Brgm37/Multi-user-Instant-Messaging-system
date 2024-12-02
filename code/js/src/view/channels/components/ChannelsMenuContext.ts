import {Channel} from "../../../model/Channel";
import {Context, createContext} from "react";
import {HasMore} from "../../../service/utils/hooks/useScroll/UseScroll";

/**
 * Context for the channels menu
 *
 * @property isSearching - Whether the user is searching for channels
 * @method onSearch - Search for channels
 * @method onCancelSearch - Cancel the search
 */
export interface ChannelsMenuContext {

    isSearching: boolean,

    onSearch(query: string): void,

    onCancelSearch(): void
}

const defaultChannelsMenuContext: ChannelsMenuContext = {
    isSearching: false,
    onSearch() {
        throw Error("Not implemented")
    },
    onCancelSearch() {
        throw Error("Not implemented")
    }
}


export const ChannelsMenuContext: Context<ChannelsMenuContext> =
    createContext<ChannelsMenuContext>(defaultChannelsMenuContext)