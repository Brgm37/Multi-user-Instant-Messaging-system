import * as React from "react";
import useChannels from "./hooks/UseChannels";
import {ChannelsState} from "./hooks/state/ChannelsState";
import ChannelsLoadedView from "./components/ChannelsLoadedView";
import ChannelsViewWithoutSearch from "./components/ChannelsViewWithoutSearch";
import ChannelsErrorView from "./components/ChannelsErrorView";
import {InfiniteScrollContext} from "../components/infiniteScroll/InfiniteScrollContext";
import {Channel} from "../../model/Channel";
import {Link} from "react-router-dom";
import {ChannelsMenuContext} from "./components/ChannelsMenuContext";

export function ChannelsView(): React.JSX.Element {
    const [state, handler] = useChannels()
    const switchCase = (state: ChannelsState) => {
        switch (state.tag) {
            case "idle": {
                handler.loadChannels()
                return <div>Loading</div>
            }
            case "error":
                return <ChannelsErrorView/>
            case "searching":
                return <ChannelsViewWithoutSearch/>
            default: {
                const value: InfiniteScrollContext<Channel> = {
                    list: state.channels.list as Channel[],
                    listMaxSize: state.channels.max,
                    isLoading: state.tag === "loading" && state.at !== "both" ? state.at : false,
                    loadMore: state.tag === "searchResults" ?
                        (offset: number, at) => handler.searchMore(state.query, offset, at) :
                        handler.loadMore,
                    hasMore: state.channels.hasMore,
                    renderItems(item: Channel): React.ReactNode {
                        return (
                            <Link to={`/channels/${item.id}`}>{item.name}</Link>
                        )
                    }
                }
                const menuBar = {
                    isSearching: state.tag === "loading",
                    onSearch: handler.search,
                    onCancelSearch: handler.clear
                }
                return (
                    <ChannelsMenuContext.Provider value={menuBar}>
                        <InfiniteScrollContext.Provider value={value}>
                            <ChannelsLoadedView/>
                        </InfiniteScrollContext.Provider>
                    </ChannelsMenuContext.Provider>
                )
            }
        }
    }
    return (
        <div>
            <h1>
                Welcome to ChIMP
            </h1>
            {switchCase(state)}
        </div>
    )
}