import {urlBuilder} from "../utils/UrlBuilder";
import useSignal from "../utils/hooks/useSignal/useSignal";
import React from "react";
import {Either, failure, success} from "../../model/Either";
import {Channel} from "../../view/findChannels/model/PublicChannel";
import {FindChannelsServiceContext} from "./FindChannelsServiceContext";

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

export function FindChannelsServiceProvider({ children }: { children: React.ReactNode }): React.JSX.Element {
    const signal = useSignal()
    const service : FindChannelsServiceContext = {
        async getChannelsByPartialName(partialName: string): Promise<Either<Channel[], string>> {
            const init: RequestInit = {
                method: "GET",
                headers: {"Content-Type": "application/json"},
                signal,
                credentials: "include"
            }
            const response = await fetch(getChannelsByPartialNameApiUrl + '/' + partialName, init);
            if (response.ok) {
                return success(await response.json()) as Either<Channel[], string>
            } else {
                return success(await response.text()) as Either<Channel[], string>
            }
        },
        async joinChannel(channelId: number): Promise<Either<void, string>> {
            const init: RequestInit = {
                method: "PUT",
                headers: {"Content-Type": "application/json"},
                signal,
                credentials: "include"
            }
            const response = await fetch(joinChannelApiUrl + '/' + channelId, init);
            if (response.ok) {
                return success(undefined) as Either<void, string>
            } else {
                return failure(await response.text()) as Either<void, string>
            }
        },
        async getPublicChannels(offset: number, limit: number): Promise<Either<Channel[], string>> {
            const init: RequestInit = {
                method: "GET",
                headers: {"Content-Type": "application/json"},
                signal,
                credentials: "include"
            }
            const response = await fetch(getPublicChannelsApiUrl + '?offset=' + offset + '&limit=' + limit, init);
            if (response.ok) {
                return success(await response.json()) as Either<Channel[], string>
            } else {
                return failure(await response.text()) as Either<Channel[], string>
            }
        }
    }
    return (
        <FindChannelsServiceContext.Provider value={service}>
            {children}
        </FindChannelsServiceContext.Provider>
    )
}