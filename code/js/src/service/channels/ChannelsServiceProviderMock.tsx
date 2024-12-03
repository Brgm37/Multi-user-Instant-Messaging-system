import * as React from "react";
import {Channel} from "../../model/Channel";
import {ChannelsServiceContext} from "./ChannelsServiceContext";
import {Either, success} from "../../model/Either";

const channels:Channel[] = [
    {id: "1", name: "Channel 1", messages: [], hasMore: true},
    {id: "2", name: "Channel 2", messages: [], hasMore: true},
    {id: "3", name: "Channel 3", messages: [], hasMore: true},
    {id: "4", name: "Channel 4", messages: [], hasMore: true},
    {id: "5", name: "Channel 5", messages: [], hasMore: true},
    {id: "6", name: "Channel 6", messages: [], hasMore: true},
    {id: "7", name: "Channel 7", messages: [], hasMore: true},
    {id: "8", name: "Channel 8", messages: [], hasMore: true},
    {id: "9", name: "Channel 9", messages: [], hasMore: true},
    {id: "10", name: "Channel 10", messages: [], hasMore: true},
    {id: "11", name: "Channel 11", messages: [], hasMore: true},
    {id: "12", name: "Channel 12", messages: [], hasMore: true},
    {id: "13", name: "Channel 13", messages: [], hasMore: true},
    {id: "14", name: "Channel 14", messages: [], hasMore: true},
]

function delay(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms));
}

export function ChannelsServiceProviderMock(
    { children }: { children: React.ReactNode }
): React.ReactElement {
    const service: ChannelsServiceContext = {
        findChannels: async (offset, limit) => {
            const c = channels.slice(offset, offset + limit)
            await delay(1000)
            return success(c) as Either<Channel[], string>
        },
        findChannelsByName: async (name: string, offset, limit) => {
            const list = channels
                .filter((c) => c.name.includes(name))
                .slice(offset, offset + limit)
            return success(list) as Either<Channel[], string>
        }
    }
    return (
        <ChannelsServiceContext.Provider value={service}>
            {children}
        </ChannelsServiceContext.Provider>
    )
}