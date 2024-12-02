import React, {ReactNode} from "react";
import useSignal from "../../utils/hooks/useSignal/useSignal";
import {FindChannelsMockServiceContext} from "./FindChannelsMockServiceContext";
import {Either, failure, success} from "../../../model/Either";
import {Channel} from "../../../view/findChannels/model/PublicChannel";

const mockChannels = [
    {
        name: {name: "test", displayName: "test1"},
        id: 1,
        owner: {id: 1, username: "test1"},
    },
    {
        name: {name: "test", displayName: "test2"},
        id: 2,
        owner: {id: 2, username: "test2"},
    },
    {
        name: {name: "test", displayName: "test3"},
        id: 3,
        owner: {id: 3, username: "test3"},
    },
    {
        name: {name: "isel", displayName: "isel"},
        id: 4,
        owner: {id: 4, username: "isel"},
    },
    {
        name: {name: "chelas", displayName: "chelas"},
        id: 5,
        owner: {id: 5, username: "chelas"},
    }
]


export function FindChannelsMockServiceProvider(props: { children: ReactNode }): React.JSX.Element {
    const signal = useSignal()
    const service : FindChannelsMockServiceContext = {
        async getChannelsByPartialName(partialName: string): Promise<Either<Channel[], string>> {
            if (partialName === "error") {
                return failure("error") as Either<Channel[], string>;
            }
            if (partialName) {
                const filteredChannels = mockChannels.filter(channel =>
                    channel.name.displayName.includes(partialName)
                );
                return success(filteredChannels) as Either<Channel[], string>
            }
            else {
                return failure("error") as Either<Channel[], string>;
            }
        },
        async joinChannel(channelId: number): Promise<Either<void, string>> {
            if (channelId === 1) {
                return success(undefined) as Either<void, string>
            } else {
                return failure("Something went Wrong") as Either<void, string>
            }
        },
        async getPublicChannels(offset: number, limit: number): Promise<Either<Channel[], string>> {
            return success([...mockChannels]) as Either<Channel[], string>
        }
    }
    return (
        <FindChannelsMockServiceContext.Provider value={service}>
            {props.children}
        </FindChannelsMockServiceContext.Provider>
    )
}