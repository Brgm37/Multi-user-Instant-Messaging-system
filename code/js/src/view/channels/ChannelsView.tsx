import * as React from "react";
import useChannels from "./hooks/useChannels/UseChannels";
import {InfiniteScrollContext} from "../components/infiniteScroll/InfiniteScrollContext";
import {Channel} from "../../model/Channel";
import BasicChannelsView from "./components/BasicChannelsView";
import {Link} from "react-router-dom";

export function ChannelsView(): React.JSX.Element {
    const [state, channels, handler] = useChannels()
    if (state.tag === "idle") handler.loadChannels()
    const provider: InfiniteScrollContext<Channel> = {
        isLoading: state.tag !== "loading" || state.at === "both" ? false : state.at,
        items: channels,
        loadMore: handler.loadMore,
        renderItems(item: Channel): React.ReactNode {
            return (
                <div
                    key={item.id}
                    className={"text-center p-2 text-gray-200 hover:bg-gray-800"}
                >
                    <Link to={"/channels/" + item.id}>{item.name}</Link>
                </div>
            )
        },
    }
    return (
        <InfiniteScrollContext.Provider value={provider}>
            <BasicChannelsView
                error={state.tag === "error" ? state.message : undefined}
                errorDismiss={handler.goBack}
            />
        </InfiniteScrollContext.Provider>
    )
}