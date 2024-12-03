import * as React from "react";
import {useChannel} from "./hooks/UseChannel";
import {ChannelState} from "./hooks/states/ChannelState";
import {InitLoadingView} from "./components/InitLoadingView";
import {ChannelErrorView} from "./components/ChannelErrorView";
import {AuthValidator} from "../session/authValidator";
import {InfiniteScrollContext} from "../components/infiniteScroll/InfiniteScrollContext";
import {Message} from "../../model/Message";
import InfiniteScroll from "../components/infiniteScroll/InfiniteScroll";
import {InputText} from "./components/InputText";

function ChannelView(): React.JSX.Element {
    const [state, handler] = useChannel()
    const switchCase = (state: ChannelState) => {
        switch (state.tag) {
            case "idle": {
                handler.initChannel()
                return <InitLoadingView/>
            }
            case "error":
                return <ChannelErrorView/>
            default: {
                const value: InfiniteScrollContext<Message> = {
                    list: state.messages.list,
                    hasMore: state.messages.hasMore,
                    isLoading: state.tag === "loading" && state.at !== "sending" ? state.at : false,
                    listMaxSize: state.messages.max,
                    loadMore(_ ,at: "head" | "tail"): void {handler.loadMore(at)},
                    renderItems(item: Message): React.ReactNode {
                        return (
                            <div>
                                <div>{item.owner.username}</div>
                                <div>{item.text}</div>
                            </div>
                        )
                    },
                }
                return (
                    <InfiniteScrollContext.Provider value={value}>
                        <InfiniteScroll/>
                        <InputText onSubmit={handler.sendMsg}/>
                    </InfiniteScrollContext.Provider>
                )
            }
        }
    }
    return (
        <div>
            <h1>Channel</h1>
            {switchCase(state)}
        </div>
    )
}

export default function (): React.JSX.Element {
    return (
        <AuthValidator>
            <ChannelView/>
        </AuthValidator>
    )
}