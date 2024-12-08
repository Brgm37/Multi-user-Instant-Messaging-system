import {urlBuilder} from "../utils/UrlBuilder";
import React from "react";
import useSignal from "../utils/hooks/useSignal/useSignal";
import {Either, failure, success} from "../../model/Either";
import {JoinChannelServiceContext} from "./JoinChannelServiceContext";

/**
 * The URL for the channel.
 */
const baseUrl = urlBuilder("/channels")

/**
 * The join channel service provider.
 */
export function JoinChannelServiceProvider(
    { children }: { children: React.ReactNode }
): React.JSX.Element {
    const signal = useSignal()
    const service: JoinChannelServiceContext = {
        async joinChannel(
            invitationToken: string
        ): Promise<Either<{ id: string }, string>> {
            const body = {
                "invitationCode": invitationToken
            }
            const init: RequestInit = {
                method: "PUT",
                headers: {"Content-Type": "application/json"},
                signal,
                credentials: "include",
                body: JSON.stringify(body)
            }
            const url = baseUrl + '/invitations'
            const response = await fetch(url, init);
            if (response.ok) {
                const data = await response.json()
                return success(data.id) as Either<{ id: string }, string>
            } else {
                return failure(await response.text()) as Either<{ id: string }, string>
            }
        }
    }
    return (
        <JoinChannelServiceContext.Provider value={service}>
            {children}
        </JoinChannelServiceContext.Provider>
    )
}