import * as React from "react";
import {useChannel} from "./hooks/UseChannel";
import {InitLoadingView} from "./components/InitLoadingView";
import {InfiniteScrollContext} from "../components/infiniteScroll/InfiniteScrollContext";
import {Message} from "../../model/Message";
import BasicChannelView from "./components/BasicChannelView";
import {InfiniteMessageScrollContext} from "./components/messageInfiniteScroll/InfiniteMessageScrollContext";

export function ChannelView(): React.JSX.Element {
    const [state, messages, handler] = useChannel()

    if (state.tag === "idle") {
        handler.initChannel()
        return <InitLoadingView/>
    }

    const provider: InfiniteScrollContext<Message> = {
        isLoading: state.tag === "loading" ? state.at : false,
        items: messages,
        renderItems(item: Message): React.ReactNode {
            return (
                <div key={item.id} className={"message"}>
                    <div className={"message-content"}>
                        <div className={"message-author"}>{item.owner.username}</div>
                        <div className={"message-text"}>{item.text}</div>
                    </div>
                </div>
            )
        },
        loadMore(_: number, at: "head" | "tail"): void {handler.loadMore(at)}
    }

    const onMessage: InfiniteMessageScrollContext = {
        onNewMessage() {
            handler.reset()
        }
    }

    return (
        <InfiniteScrollContext.Provider value={provider}>
            <InfiniteMessageScrollContext.Provider value={onMessage}>
                <BasicChannelView
                    error={state.tag === "error" ? state.message : undefined}
                    errorDismiss={handler.goBack}
                    onSend={handler.sendMsg}
                    onError={handler.error}
                />
            </InfiniteMessageScrollContext.Provider>
        </InfiniteScrollContext.Provider>
    )
}
