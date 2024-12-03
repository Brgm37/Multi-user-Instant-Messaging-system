import * as React from "react";
import {Either, success, failure} from "../../model/Either";
import {Channel, jsonToChannel} from "../../model/Channel";
import {ChannelsServiceContext} from "./ChannelsServiceContext";
import {urlBuilder} from "../utils/UrlBuilder";

const urlBase = urlBuilder("/channels")

export function ChannelsServicesProvider(
    {children}: { children: React.ReactNode }
): React.ReactElement {
    const service: ChannelsServiceContext = {
        findChannels: async (offset: number, limit: number): Promise<Either<Channel[], string>> => {
            const url = urlBase + `/my?offset=${offset}&limit=${limit}`
            const response = await fetch(url)
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
        ,
        findChannelsByName: async (name: string, offset: number, limit: number): Promise<Either<Channel[], string>> => {
            const url = urlBase + `/my/${name}?offset=${offset}&limit=${limit}`
            const response = await fetch(url)
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