import * as React from "react";
import {useChannel} from "./hooks/UseChannel";
import {InitLoadingView} from "./components/InitLoadingView";
import "../../styles/InfiniteScrollChannel.css";
import {InfiniteScrollContext} from "../components/infiniteScroll/InfiniteScrollContext";
import {Message} from "../../model/Message";
import BasicChannelView from "./components/BasicChannelView";

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

    return (
        <InfiniteScrollContext.Provider value={provider}>
            <BasicChannelView
                error={state.tag === "error" ? state.message : undefined}
                errorDismiss={handler.goBack}
                onSend={handler.sendMsg}
            />
        </InfiniteScrollContext.Provider>
    )
}
