import {PublicChannel} from "../../model/PublicChannel";

/**
 * @description Type for the state of the PublicChannels form.
 *
 * @type FindChannelState
 * @prop searchBar The search bar input.
 * @prop channels The list of public channels.
 */
export type FindChannelState =
    { tag:
            "navigating" |
            "searching" | "fetchingMore"
        searchBar: string, channels: PublicChannel[],
    } |
    { tag: "error", error: string, channels: PublicChannel[] } |
    { tag: "joining", channels: PublicChannel[], searchBar: string, channelId: number } |
    { tag: "redirect", channelId: number }