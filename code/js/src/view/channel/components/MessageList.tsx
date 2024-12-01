import * as React from "react";
import {useCallback, useContext, useRef} from "react";
import {MessageViewContext} from "./MessageViewContext";
import {InputText} from "./InputText";

/**
 * The view for the message list.
 *
 * @returns React.JSX.Element
 */
export function MessageList(): React.JSX.Element {
    const context = useContext(MessageViewContext);
    const observer = useRef<IntersectionObserver | null>(null);
    const lastItemRef = useCallback(
        (node: HTMLDivElement) => {
            if (observer.current) observer.current.disconnect();
            observer
                .current = new IntersectionObserver((entries) => {
                    if (entries[0].isIntersecting && context.hasMore) context.loadMore();
                }
            )
            if (node) observer.current.observe(node);
        },
        [context.messages]
    )
    return (
        <div>
            { context.isLoadingMore && <p>Loading...</p> }
            {
                context.messages
                    .sort((a, b) => a.timestamp - b.timestamp)
                    .map((message, index) => {
                            if (context.messages.length === index + 1) {
                                return (
                                    <p key={message.id} ref={lastItemRef}>
                                        {message.text}
                                    </p>
                                )
                            } else {
                                return (
                                    <p key={message.id}>
                                        {message.text}
                                    </p>
                                )
                            }
                        }
                    )
            }
            { context.isSending && <p>Sending...</p> }
            <InputText onSubmit={context.sendMsg}/>
        </div>
    )
}