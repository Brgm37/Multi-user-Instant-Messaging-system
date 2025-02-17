import * as React from 'react';
import {ReactNode} from "react";
import useSignal from "../utils/hooks/useSignal/useSignal";
import {EditChannelServiceContext} from "./EditChannelServiceContext";
import {Either, success} from "../../model/Either";
import {urlBuilder} from "../utils/UrlBuilder";
import {Channel, jsonToChannel} from "../../model/Channel";
import {ChannelVisibility} from "../../model/ChannelVisibility";

/**
 * The URL for the channel.
 */
const urlBase = urlBuilder("/channels")

/**
 * The edit channel service provider.
 */
export function EditChannelServiceProvider(
    {children}: { children: ReactNode }
): React.JSX.Element {
    const signal = useSignal()

    const service: EditChannelServiceContext = {
        async loadChannel(id: string): Promise<Either<Channel, string>> {
            const init: RequestInit = {
                method: "GET",
                signal,
                credentials: "include"
            }
            const response = await fetch(`${urlBase}/${id}`, init)
            if (response.ok) {
                const data = await response.json()
                return success(jsonToChannel(data)) as Either<Channel, string>
            } else {
                return success(await response.text()) as Either<Channel, string>
            }
        },
        async editChannel(
            id: string,
            description?: string,
            visibility?: ChannelVisibility,
            icon?: string
        ): Promise<Either<void, string>> {
            const body: any = {}
            if (description) body["description"] = description
            if (visibility) body["visibility"] = visibility
            if (icon) body["icon"] = icon
            const init: RequestInit = {
                method: "PUT",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(body),
                signal,
                credentials: "include"
            }
            const response = await fetch(`${urlBase}/${id}`, init)
            if (response.ok) return success(undefined) as Either<void, string>
            else return success(await response.text()) as Either<void, string>
        }
    }
    return (
        <EditChannelServiceContext.Provider value={service}>
            {children}
        </EditChannelServiceContext.Provider>
    )
}