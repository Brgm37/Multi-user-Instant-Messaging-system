import * as React from "react";
import {ChannelServiceContext} from "./ChannelServiceContext";
import useSignal from "../utils/hooks/useSignal/useSignal";
import {urlBuilder} from "../utils/UrlBuilder";
import {Either, success, failure} from "../../model/Either";
import {Channel, jsonToChannel} from "../../model/Channel";
import {jsonToMessage, Message} from "../../model/Message";

/**
 * The URL for the channel API.
 */
const channelApiUrl = urlBuilder("/channels")

/**
 * The URL for the message API.
 */
const messageApiUrl = urlBuilder("/messages")

/**
 * The headers for the message.
 */
const msgHeaders = "msg"

/**
 * The headers for the channel.
 */
const channelHeaders = "channel"

export function ChannelServiceProvider({children}: { children: React.ReactNode }): React.JSX.Element {
    const signal = useSignal()
    const service: ChannelServiceContext = {
        async loadChannel(cId: string): Promise<Either<Channel, string>> {
            const url = channelApiUrl + "/" + cId
            const init: RequestInit = {
                method: "GET",
                headers: {"Content-Type": "application/json"},
                signal,
                credentials: "include"
            }
            const response = await fetch(url, init);
            if (response.ok) {
                response
                    .json()
                    .then((json) => {
                        return success(jsonToChannel(json)) as Either<Channel, string>
                    })
            } else {
                return failure(response.text()) as Either<Channel, string>
            }
        },
        async loadMore(cId: string, timestamp: string, limit: number): Promise<Either<Message[], string>> {
            const url = `${messageApiUrl}/${cId}/${timestamp}?limit=${limit}`
            const init: RequestInit = {
                method: "GET",
                headers: {"Content-Type": "application/json"},
                signal,
                credentials: "include"
            }
            const response = await fetch(url, init);
            if (response.ok) {
                response
                    .json()
                    .then((json) => {
                        const messages = json
                        messages.map((msg: any) => jsonToMessage(msg))
                        return success(messages) as Either<Message[], string>
                    })
            } else {
                return failure(response.text()) as Either<Message[], string>
            }
        },
        async sendMsg(cId: string, msg: string): Promise<Either<void, string>> {
            const init: RequestInit = {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({[msgHeaders]: msg, [channelHeaders]: cId}),
                signal,
                credentials: "include"
            }
            const response = await fetch(messageApiUrl, init);
            if (response.ok) {
                return success(undefined) as Either<void, string>
            } else {
                return failure(response.text()) as Either<void, string>
            }
        }
    }
    return (
        <ChannelServiceContext.Provider value={service}>
            {children}
        </ChannelServiceContext.Provider>
    )
}