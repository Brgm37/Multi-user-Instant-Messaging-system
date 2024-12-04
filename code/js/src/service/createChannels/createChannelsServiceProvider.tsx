import {urlBuilder} from "../utils/UrlBuilder";
import React, {useContext} from "react";
import {CreateChannelsServiceContext} from "./createChannelsServiceContext";
import {Either, failure, success} from "../../model/Either";
import {Channel, jsonToChannel} from "../../model/Channel";
import {AuthUserContext} from "../../view/session/AuthUserContext";
import useSignal from "../utils/hooks/useSignal/useSignal";
import {CreateChannel} from "../../view/createChannels/model/CreateChannel";

const urlBase = urlBuilder("/channels")

const channelNameHeader = "name"
const visibilityHeader = "visibility"
const accessControlHeader = "accessControl"

export function ChannelServiceProvider(
    {children}: { children: React.ReactNode }
): React.JSX.Element {
    const signal = useSignal()
    const service: CreateChannelsServiceContext = {

        async createChannel(
            name: string,
            visibility: string,
            accessControl: string
        ): Promise<Either<CreateChannel, string>> {
            const init: RequestInit = {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({
                    [channelNameHeader]: name,
                    [visibilityHeader]: visibility,
                    [accessControlHeader]: accessControl
                }),
                signal,
                credentials: "include"
            }
            const response = await fetch(urlBase, init)
            if (response.ok) {
                const data = await response.json()
                return success(jsonToChannel(data)) as Either<CreateChannel, string>
            } else {
                const error = await response.text()
                return failure(error) as Either<CreateChannel, string>
            }

        },

        findChannelByName: async (name: string): Promise<Either<Channel, string>> => {
            const url = urlBase + `/my/${name}`
            const user = useContext(AuthUserContext)
            const response = await fetch(url)
            if (response.ok) {
                const channels = await response.json()
                return success(
                    channels.map((c: any) => {
                        jsonToChannel(
                            c.filter((c: any) => {
                                c.owner.id.toString() == user.id
                            })
                        )
                    })
                ) as Either<Channel, string>
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