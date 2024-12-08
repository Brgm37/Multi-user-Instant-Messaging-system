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
                const data = await response.json()
                const accessRequest = await fetch(`${channelApiUrl}/accessControl/${cId}`, init)
                const access = await accessRequest.json()
                data.access = access.accessControl
                return success(jsonToChannel(data)) as Either<Channel, string>
            } else {
                const error = await response.text()
                return failure(error) as Either<Channel, string>
            }
        },
        async loadMore(
            cId: string,
            timestamp: string,
            limit: number,
            at: "before" | "after"
        ): Promise<Either<Message[], string>> {
            let url = `${messageApiUrl}/channel/${cId}/timestamp`
            if (timestamp === "0") url = `${url}?limit=${limit}`
            else url = `${url}?timestamp=${encodeURIComponent(timestamp)}&limit=${limit}`
            url = `${url}&isBefore=${at === "before"}`
            const init: RequestInit = {
                method: "GET",
                headers: {"Content-Type": "application/json"},
                signal,
                credentials: "include"
            }
            const response = await fetch(url, init);
            if (response.ok) {
                const messages = await response.json()
                return success(
                    messages.map((msg: any) => {
                        return jsonToMessage(msg)
                    })
                ) as Either<Message[], string>
            } else {
                const error = await response.text()
                return failure(error) as Either<Message[], string>
            }
        },
        async sendMsg(cId: string, msg: string): Promise<Either<Message, string>> {
            const init: RequestInit = {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({[msgHeaders]: msg, [channelHeaders]: Number(cId)}),
                signal,
                credentials: "include"
            }
            const response = await fetch(messageApiUrl, init);
            if (response.ok) {
                const data = await response.json()
                return success(jsonToMessage(data)) as Either<Message, string>
            } else {
                const error = await response.text()
                return failure(error) as Either<Message, string>
            }
        },
        async leaveOrDelete(cId: string): Promise<Either<void, string>> {
            const init: RequestInit = {
                method: "DELETE",
                headers: {"Content-Type": "application/json"},
                signal,
                credentials: "include"
            }
            const url = `${channelApiUrl}/${cId}`
            const response = await fetch(url, init);
            if (response.ok) {
                return success(undefined) as Either<void, string>
            } else {
                const error = await response.text()
                return failure(error) as Either<void, string>
            }
        }
    }
    return (
        <ChannelServiceContext.Provider value={service}>
            {children}
        </ChannelServiceContext.Provider>
    )
}