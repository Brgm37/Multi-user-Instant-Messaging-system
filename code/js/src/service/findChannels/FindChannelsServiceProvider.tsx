import {urlBuilder} from "../utils/UrlBuilder";
import useSignal from "../utils/hooks/useSignal/useSignal";
import React from "react";
import {Either, failure, success} from "../../model/Either";
import {FindChannelsServiceContext} from "./FindChannelsServiceContext";
import {Channel, jsonToPublicChannel} from "../../model/Channel";
import {PublicChannel} from "../../model/PublicChannel";

const baseUrl = urlBuilder("/channels")

export function FindChannelsServiceProvider({ children }: { children: React.ReactNode }): React.JSX.Element {
    const signal = useSignal()
    const service : FindChannelsServiceContext = {
        async getChannelsByPartialName(partialName: string, offset: number, limit: number): Promise<Either<PublicChannel[], string>> {
            const init: RequestInit = {
                method: "GET",
                headers: {"Content-Type": "application/json"},
                signal,
                credentials: "include"
            }
            const url = baseUrl + '/search/' + partialName + `?offset=${offset}&limit=${limit}`
            const response = await fetch(url, init);
            if (response.ok) {
                const channels = await response.json()
                return success(
                    channels.map((c: any) => {
                        return jsonToPublicChannel(c)
                    })
                ) as Either<PublicChannel[], string>
            } else {
                return failure(await response.text()) as Either<PublicChannel[], string>
            }
        },
        async joinChannel(channelId: number): Promise<Either<void, string>> {
            const init: RequestInit = {
                method: "PUT",
                headers: {"Content-Type": "application/json"},
                signal,
                credentials: "include"
            }
            const url = baseUrl + '/' + channelId
            const response = await fetch(url, init);
            if (response.ok) {
                return success(undefined) as Either<void, string>
            } else {
                return failure(await response.text()) as Either<void, string>
            }
        },
        async getPublicChannels(offset: number, limit: number): Promise<Either<PublicChannel[], string>> {
            const init: RequestInit = {
                method: "GET",
                headers: {"Content-Type": "application/json"},
                signal,
                credentials: "include"
            }
            const response = await fetch(baseUrl + '?offset=' + offset + '&limit=' + limit, init);
            if (response.ok) {
                const channels = await response.json()
                return success(
                    channels.map((c: any) => {
                        return jsonToPublicChannel(c)
                    })
                )
            } else {
                return failure(await response.text())
            }
        }
    }
    return (
        <FindChannelsServiceContext.Provider value={service}>
            {children}
        </FindChannelsServiceContext.Provider>
    )
}