import * as React from 'react';
import {SseCommunicationService, SseCommunicationServiceContext} from "./SseCommunicationService";
import {urlBuilder} from "../utils/UrlBuilder";
import {jsonToMessage, Message} from "../../model/Message";
import {useEffect, useState} from "react";

/**
 * The URL for the messages.
 */
const msgSseUrl = urlBuilder("/messages/sse");

/**
 * The ping interval.
 */
const PING_INTERVAL = 25000; // 25 seconds

/**
 * The SSE communication service provider.
 */
export function SseCommunicationServiceProvider(
    {children}: { children: React.ReactNode }
): React.JSX.Element {
    const [messages, setMessages] = useState<Message[]>([]);
    let pingInterval: NodeJS.Timeout;

    const startPing = (sse: EventSource) => {
        if (pingInterval) clearInterval(pingInterval);
        pingInterval = setInterval(() => {
            if (sse.readyState === sse.OPEN) {
                console.log("Sending ping");
                sse.dispatchEvent(new Event('ping'));
            }
        }, PING_INTERVAL);
    };

    const connectSse = () => {
        const newSseMsg = new EventSource(msgSseUrl, {withCredentials: true});
        newSseMsg.onopen = () => {
            console.log("SSE connection opened");
            startPing(newSseMsg);
        };
        newSseMsg.onerror = () => {
            console.log("SSE connection closed");
            newSseMsg.close();
            clearInterval(pingInterval);
        };
        newSseMsg.onmessage = (event) => {
            const msg = jsonToMessage(JSON.parse(event.data));
            setMessages((prevMessages) => [...prevMessages, msg]);
        };
        return newSseMsg;
    };

    useEffect(() => {
        const sse = connectSse();
        return () => {
            sse.close();
            clearInterval(pingInterval);
        };
    }, []);

    const service: SseCommunicationService = {
        messages: messages,
        consumeMessage(msgList: Message[]): void {
            const newMessages = messages.filter((msg) => !msgList.includes(msg));
            setMessages(newMessages);
        }
    };

    return (
        <SseCommunicationServiceContext.Provider value={service}>
            {children}
        </SseCommunicationServiceContext.Provider>
    );
}