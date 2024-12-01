import * as React from "react";
import {useChannel} from "./hooks/UseChannel";
import {ChannelState} from "./hooks/states/ChannelState";
import {InitLoadingView} from "./components/InitLoadingView";
//import {MessageList} from "./components/MessageList";
import {ChannelErrorView} from "./components/ChannelErrorView";
import {MessageViewContext} from "./components/MessageViewContext";
import {AuthValidator} from "../session/authValidator";

function ChannelView(): React.JSX.Element {
    const [state, handler] = useChannel()
    const switchCase = (state: ChannelState) => {
        switch (state.tag) {
            case "idle": {
                handler.initChannel()
                return <InitLoadingView/>
            }
            case "init":
                return <InitLoadingView/>
            case "error":
                return <ChannelErrorView/>
            default: {
                const provider: MessageViewContext = {
                    hasMore: state.hasMore,
                    isLoadingMore: state.tag === "loading" && state.intent.includes("loadMore"),
                    isSending: state.tag === "loading" && state.intent.includes("sendMessage"),
                    messages: state.messages,
                    loadMore: handler.loadMore,
                    sendMsg: handler.sendMsg
                }
                return (
                    <MessageViewContext.Provider value={provider}>
                        {/*<MessageList/>*/}
                        <div>Hello</div>
                    </MessageViewContext.Provider>
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