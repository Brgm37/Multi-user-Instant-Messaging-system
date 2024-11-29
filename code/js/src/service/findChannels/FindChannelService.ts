import {PublicChannel} from "../../view/findChannels/model/PublicChannel";
import {urlBuilder} from "../utils/UrlBuilder";
import {useFetch} from "../utils/useFetch";

/**
 * Interface for the FindChannels service.
 *
 * @method getChannelsByPartialName
 * @method joinChannel
 */
export type FindChannelsService = {

    getChannelsByPartialName(
        partialName: string,
        onSuccess: (response: Response) => void,
        onError: (error: Error) => void
    ): void

    joinChannel(
        channelId: number,
        onSuccess: (response: Response) => void,
        onError: (error: Error) => void
    ): void

    getPublicChannels(
        offset: number,
        limit: number,
        onSuccess: (response: Response) => void,
        onError: (error: Error) => void
    ): void
}

/**
 * The URL for the find channels API.
 */
const getChannelsByPartialNameApiUrl = urlBuilder("/channels/search")

/**
 * The URL for the join channel API.
 */
const joinChannelApiUrl = urlBuilder("/users/channels")

/**
 * The URL for the public channels API.
 */
const getPublicChannelsApiUrl = urlBuilder("/channels")

/**
 * The default find channel service.
 *
 * @returns FindChannelsService
 */
export function makeDefaultFindChannelService() : FindChannelsService {
    return {
        getChannelsByPartialName: (partialName, onSuccess, onError) => {
            const fetchHandler = useFetch(getChannelsByPartialNameApiUrl + '/' + partialName, "GET")
            fetchHandler.onSuccessChange(onSuccess)
            fetchHandler.onErrorChange(onError)
            fetchHandler.toFetch()
        },
        joinChannel: (channelId, onSuccess, onError) => {
            const fetchHandler = useFetch(joinChannelApiUrl + '/' + channelId, "PUT")
            fetchHandler.onSuccessChange(onSuccess)
            fetchHandler.onErrorChange(onError)
            fetchHandler.toFetch()
        },

        getPublicChannels: (offset, limit, onSuccess, onError) => {
            const fetchHandler = useFetch(getPublicChannelsApiUrl + '?offset=' + offset + '&limit=' + limit, "GET")
            fetchHandler.onSuccessChange(onSuccess)
            fetchHandler.onErrorChange(onError)
            fetchHandler.toFetch()
        }
    }
}