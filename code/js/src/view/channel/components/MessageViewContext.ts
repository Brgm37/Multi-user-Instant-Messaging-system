import * as React from "react";
import {Message} from "../../../model/Message";

/**
 * The context for the message view.
 *
 * @prop messages The messages to display.
 * @prop hasMore Whether there are more messages to load.
 * @prop isLoadingMore Whether the component is loading more messages.
 * @prop isSending Whether the component is sending a message.
 * @method loadMore The function to load more messages.
 * @method sendMsg The function to send a message.
 */
export interface MessageViewContext {
    messages: Message[],
    hasMore: boolean,
    isLoadingMore: boolean,
    isSending: boolean,
    loadMore () : void,
    sendMsg (msg: string): void
}

const defaultMessageViewContext: MessageViewContext = {
    messages: [],
    hasMore: false,
    isLoadingMore: false,
    isSending: false,
    loadMore: () => { throw Error('loadMore function not implemented') },
    sendMsg: () => { throw Error('sendMsg function not implemented') }
}

/**
 * The context for the message view.
 */
export const MessageViewContext =
    React
        .createContext<MessageViewContext>(defaultMessageViewContext)