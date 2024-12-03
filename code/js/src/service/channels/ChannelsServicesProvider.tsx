import * as React from "react";
import {Either, success, failure} from "../../model/Either";
import {Channel} from "../../model/Channel";
import {ChannelsServiceContext} from "./ChannelsServiceContext";

export function ChannelsServicesProvider(
    { children }: { children: React.ReactNode }
): React.ReactElement {
    const service: ChannelsServiceContext = {
        findChannels: async (offset: number, limit: number): Promise<Either<Channel[], string>> => {
            throw Error("Not implemented");
        }
        ,
        findChannelsByName: async (name: string, offset: number, limit: number): Promise<Either<Channel[], string>> => {
            throw Error("Not implemented");
        }
    }
    return (
        <ChannelsServiceContext.Provider value = {service} >
            <{children}>
        </ChannelsServiceContext.Provider>
    )
}