import {urlBuilder} from "../utils/UrlBuilder";
import useSignal from "../utils/hooks/useSignal/useSignal";
import React from "react";
import {Either, failure, success} from "../../model/Either";
import {FindChannelsServiceContext} from "./FindChannelsServiceContext";
import {jsonToPublicChannel} from "../../model/Channel";
import {PublicChannel} from "../../model/PublicChannel";

/**
 * The URL for the channel.
 */
const baseUrl = urlBuilder("/channels")

/**
 * The find channels service provider.
 */
export function FindChannelsServiceProvider({children}: { children: React.ReactNode }): React.JSX.Element {
    const signal = useSignal()

    React.useEffect(() => {
        const handleAbort = () => {
            console.log("Request aborted");
            // Do nothing
        };

        signal.addEventListener("abort", handleAbort);

        return () => {
            signal.removeEventListener("abort", handleAbort);
        };
    }, [signal]);

    const service: FindChannelsServiceContext = {
        async getChannelsByPartialName(partialName: string, offset: number, limit: number): Promise<Either<PublicChannel[], string>> {
            const init: RequestInit = {
                method: "GET",
                headers: {"Content-Type": "application/json"},
                signal,
                credentials: "include"
            }
            const url = baseUrl + '/public/' + partialName + `?offset=${offset}&limit=${limit}`
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
        async joinChannel(cId: number): Promise<Either<void, string>> {
            const init: RequestInit = {
                method: "PUT",
                headers: {"Content-Type": "application/json"},
                signal,
                credentials: "include",
                body: JSON.stringify({cId})
            }
            const url = baseUrl + '/invitations'
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
            const response = await fetch(baseUrl + '/public' + '?offset=' + offset + '&limit=' + limit, init);
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