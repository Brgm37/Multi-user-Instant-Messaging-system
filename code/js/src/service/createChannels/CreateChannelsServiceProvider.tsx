import {urlBuilder} from "../utils/UrlBuilder";
import React, {useContext} from "react";
import {CreateChannelsServiceContext} from "./CreateChannelsServiceContext";
import {Either, failure, success} from "../../model/Either";
import {Channel, jsonToChannel} from "../../model/Channel";
import {AuthUserContext} from "../../view/session/AuthUserContext";
import useSignal from "../utils/hooks/useSignal/useSignal";
import {ChannelVisibility} from "../../model/ChannelVisibility";
import {AccessControl} from "../../model/AccessControl";

/**
 * The URL for the channel.
 */
const urlBase = urlBuilder("/channels")

/**
 * The default icon source.
 */
const defaultIconSrc = "/defaultIcons/default.png"

/**
 * The creation channel service provider.
 */
export function CreateChannelServiceProvider(
    {children}: { children: React.ReactNode }
): React.JSX.Element {
    const signal = useSignal()
    const user = useContext(AuthUserContext)
    const service: CreateChannelsServiceContext = {

        async createChannel(
            name: string,
            visibility: ChannelVisibility,
            accessControl: AccessControl,
            description: string | undefined = "",
            icon: string = defaultIconSrc
        ): Promise<Either<Channel, string>> {
            const init: RequestInit = {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({
                    name,
                    visibility,
                    accessControl,
                    description,
                    icon
                }),
                signal,
                credentials: "include"
            }
            const response = await fetch(urlBase, init)
            if (response.ok) {
                const data = await response.json()
                return success(jsonToChannel(data)) as Either<Channel, string>
            } else {
                const error = await response.text()
                return failure(error) as Either<Channel, string>
            }
        },

        findChannelByName: async (name: string): Promise<Either<Channel, string>> => {
            const url = urlBase + `/my/${name}`
            const init: RequestInit = {
                method: "GET",
                headers: {"Content-Type": "application/json"},
                signal,
                credentials: "include"
            }
            const response = await fetch(url, init)
            if (response.ok) {
                const channels = await response.json()
                const channelsIOwn = channels.filter((c: any) =>
                     c.owner.id === Number(user.id)
                )
                const channel = channelsIOwn.filter((c: any) => c.name.displayName.toLowerCase() === name.toLowerCase())
                if (channel.length > 0) {
                    return success(jsonToChannel(channel[0])) as Either<Channel, string>
                }else {
                    return failure("Channel not found") as Either<Channel, string>
                }
            } else {
                return failure(await response.text())
            }
        }
    }
    return (
        <CreateChannelsServiceContext.Provider value={service}>
            {children}
        </CreateChannelsServiceContext.Provider>
    )
}