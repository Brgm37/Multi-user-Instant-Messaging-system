import {PublicChannel} from "../../../../model/PublicChannel";

/**
 * @description Type for the state of the PublicChannels form.
 *
 * @type FindChannelState
 * @prop searchBar The search bar input.
 * @prop channels The list of public channels.
 */
export type FindChannelState =
    | { tag: "navigating"; searchBar: string; channels: PublicChannel[] }
    | { tag: "fetchingMore"; searchBar: string; channels: PublicChannel[] }
    | { tag: "error"; error: string; channels: PublicChannel[], searchBar: string }
    | { tag: "joining"; searchBar: string; channels: PublicChannel[]; channelId: number }
    | { tag: "redirect"; channelId: number, searchBar: string }
    | { tag: "editing"; searchBar: string; channels: PublicChannel[] };