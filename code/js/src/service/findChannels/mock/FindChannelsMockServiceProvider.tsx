import React, {ReactNode} from "react";
import useSignal from "../../utils/hooks/useSignal/useSignal";
import {FindChannelsMockServiceContext} from "./FindChannelsMockServiceContext";
import {Either, failure, success} from "../../../model/Either";
import {Channel} from "../../../view/findChannels/model/PublicChannel";

const mockChannels = [
    {
        name: {name: "test", displayName: "TEST CHANNEL 1"},
        id: 1,
        owner: {id: 1, name: "test1"},
    },
    {
        name: {name: "test", displayName: "TEST CHANNEL 2"},
        id: 2,
        owner: {id: 2, name: "test2"},
    },
    {
        name: {name: "test", displayName: "TEST CHANNEL 3"},
        id: 3,
        owner: {id: 3, name: "test3"},
    },
    {
        name: {name: "isel", displayName: "ISEL CHANNEL"},
        id: 4,
        owner: {id: 4, name: "isel"},
    },
    {
        name: {name: "chelas", displayName: "CHELAS CHANNEL"},
        id: 5,
        owner: {id: 5, name: "chelas"},
    }
]


export function FindChannelsMockServiceProvider(props: { children: ReactNode }): React.JSX.Element {
    const signal = useSignal()
    const service : FindChannelsMockServiceContext = {
        async getChannelsByPartialName(partialName: string): Promise<Either<Channel[], string>> {
            return new Promise((resolve) => {
                setTimeout(() => {
                    if (partialName === "error") {
                        resolve(failure("error") as Either<Channel[], string>);
                    } else if (partialName) {
                        const filteredChannels = mockChannels.filter(channel =>
                            channel.name.displayName.toLowerCase().includes(partialName.toLowerCase())
                        );
                        resolve(success(filteredChannels) as Either<Channel[], string>);
                    } else {
                        resolve(success([]) as Either<Channel[], string>);
                    }
                }, 1000);
            });
        },
        async joinChannel(channelId: number): Promise<Either<void, string>> {
            return new Promise((resolve) => {
                setTimeout(() => {
                    if (channelId === 1) {
                        resolve(success(undefined) as Either<void, string>)
                    } else {
                        resolve(failure("error") as Either<void, string>)
                    }
                }, 1000);
            });
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