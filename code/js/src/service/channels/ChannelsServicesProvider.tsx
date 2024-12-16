import * as React from "react";
import {Either, success, failure} from "../../model/Either";
import {Channel, jsonToChannel} from "../../model/Channel";
import {ChannelsServiceContext} from "./ChannelsServiceContext";
import {urlBuilder} from "../utils/UrlBuilder";
import useSignal from "../utils/hooks/useSignal/useSignal";
import {useNavigate} from "react-router-dom";
import configJson from "../../../envConfig.json";
import removeCookie from "../session/RemoveCookie";

/**
 * The authentication cookie.
 */
const auth_cookie = configJson.session

/**
 * The URL for the channel.
 */
const channelUrlBase = urlBuilder("/channels")

/**
 * The URL for the user.
 */
const userUrlBase = urlBuilder("/users")

/**
 * The channel service provider.
 */
export function ChannelsServicesProvider(
    {children}: { children: React.ReactNode }
): React.ReactElement {
    const signal = useSignal()
    const navigate = useNavigate()
    const getInit: RequestInit = {method: "GET", credentials: "include", signal}

    React.useEffect(() => {
        const handleAbort = () => {
            console.log("Request aborted");
            // Do nothing
        };

        signal.addEventListener("abort", handleAbort);

        return () => {
            signal.removeEventListener("abort", handleAbort);
        };
    }, [signal]);

    const service: ChannelsServiceContext = {
        async logout(): Promise<Either<void, string>> {
            const url = userUrlBase + "/logout"
            const init: RequestInit = {
                method: "DELETE",
                credentials: "include",
                signal
            }
            const response = await fetch(url, init)
            if (!response.ok) {
                return failure(await response.text()) as Either<void, string>
            } else {
                removeCookie(auth_cookie)
                navigate("/register")
                return success(undefined) as Either<void, string>
            }
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