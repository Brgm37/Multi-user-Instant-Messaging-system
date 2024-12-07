import * as React from "react";
import {Either, success, failure} from "../../model/Either";
import {Channel, jsonToChannel} from "../../model/Channel";
import {ChannelsServiceContext} from "./ChannelsServiceContext";
import {urlBuilder} from "../utils/UrlBuilder";
import useSignal from "../utils/hooks/useSignal/useSignal";
import {useNavigate} from "react-router-dom";

const channelUrlBase = urlBuilder("/channels")
const userUrlBase = urlBuilder("/users")

export function ChannelsServicesProvider(
    {children}: { children: React.ReactNode }
): React.ReactElement {
    const signal = useSignal()
    const navigate = useNavigate()
    const getInit: RequestInit = {method: "GET", credentials: "include", signal}
    const service: ChannelsServiceContext = {
        async logout(): Promise<void> {
            const url = userUrlBase + "/logout"
            const init: RequestInit = {
                method: "DELETE",
                credentials: "include",
                signal
            }
            const response = await fetch(url, init)
            if (!response.ok) throw new Error(await response.text())
            else navigate("/register")
        },
        findChannels: async (offset: number, limit: number): Promise<Either<Channel[], string>> => {
            const url = channelUrlBase + `/my?offset=${offset}&limit=${limit}`
            const response = await fetch(url, getInit)
            if (response.ok) {
                const channels = await response.json()
                return success(
                    channels.map((c: any) => {
                        return jsonToChannel(c)
                    })
                ) as Either<Channel[], string>
            } else {
                return failure(await response.text())
            }
        },
        findChannelsByName: async (name: string, offset: number, limit: number): Promise<Either<Channel[], string>> => {
            const url = channelUrlBase + `/my/${name}?offset=${offset}&limit=${limit}`
            const response = await fetch(url, getInit)
            if (response.ok) {
                const channels = await response.json()
                return success(
                    channels.map((c: any) => {
                        return jsonToChannel(c)
                    })
                ) as Either<Channel[], string>
            } else {
                return failure(await response.text())
            }
        }
    }
    return (
        <ChannelsServiceContext.Provider value={service}>
            {children}
        </ChannelsServiceContext.Provider>
    )
}