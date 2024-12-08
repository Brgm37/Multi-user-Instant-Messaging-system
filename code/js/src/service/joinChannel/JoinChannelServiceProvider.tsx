import {urlBuilder} from "../utils/UrlBuilder";
import React from "react";
import useSignal from "../utils/hooks/useSignal/useSignal";
import {Either, failure, success} from "../../model/Either";
import {JoinChannelServiceContext} from "./JoinChannelServiceContext";

const baseUrl = urlBuilder("/channels")

export function JoinChannelServiceProvider({ children }: { children: React.ReactNode }): React.JSX.Element {
    const signal = useSignal()
    const service = {
        async joinChannel(invitationToken: string): Promise<Either<void, string>> {
            const init: RequestInit = {
                method: "PUT",
                headers: {"Content-Type": "application/json"},
                signal,
                credentials: "include",
                body: JSON.stringify({invitationToken})
            }
            const url = baseUrl + '/invitations'
            const response = await fetch(url, init);
            if (response.ok) {
                return success(undefined) as Either<void, string>
            } else {
                return failure(await response.text()) as Either<void, string>
            }
        }
    }
    return (
        <JoinChannelServiceContext.Provider value={service}>
            {children}
        </JoinChannelServiceContext.Provider>
    )
}