import React, {ReactNode} from "react";
import useSignal from "../utils/useSignal";
import {FindChannelsMockServiceContext} from "./FindChannelsMockServiceContext";
import {Either} from "../../model/Either";
import {Channel} from "../../view/findChannels/model/PublicChannel";


export function FindChannelsMockServiceProvider(props: { children: ReactNode }): React.JSX.Element {
    const signal = useSignal()
    const service : FindChannelsMockServiceContext = {
        async getChannelsByPartialName(partialName: string): Promise<Either<Channel[], string>> {
            if (partialName.includes("1")) {
                return  as Either<Channel[], string>
            }

        },
        async joinChannel(channelId: number): Promise<Either<void, string>> {
            return ""
        },
        async getPublicChannels(offset: number, limit: number): Promise<Either<Channel[], string>> {
            return "[]"
        }
    }
}