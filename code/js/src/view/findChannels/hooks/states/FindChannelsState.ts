import {PublicChannel} from "../../model/findChannels/PublicChannel";

/**
 * @description Type for the state of the PublicChannels form.
 *
 * @type State
 * @prop searchBar The search bar input.
 * @prop channels The list of public channels.
 */
export type State =
    { tag:
            "navigating" |
            "searching" |
            "fetchingMore",
        searchBar: string, channels: PublicChannel[],
    } |
    { tag: "error", error: string, channels: PublicChannel[] } |
    { tag: "redirect" }
