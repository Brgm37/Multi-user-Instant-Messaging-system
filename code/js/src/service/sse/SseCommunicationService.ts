import {createContext} from "react";
import {Message} from "../../model/Message";

/**
 * The context for the server-sent event communication service.
 *
 */
export interface SseCommunicationService {
    messages: Message[]
    consumeMessage(msgList:Message[]): void
}

const defaultSseCommunicationService: SseCommunicationService = {
    messages: [],
    consumeMessage(): void {throw Error("Not implemented")}
}

export const SseCommunicationServiceContext =
    createContext<SseCommunicationService>(defaultSseCommunicationService);