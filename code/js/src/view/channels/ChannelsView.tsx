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
                    className={"flex flex-col items-center relative group"}
                >
                    <Link to={"/channels/" + item.id}>
                        <div
                            className="w-14 h-14 overflow-hidden rounded-full transition-transform duration-300 ease-in-out transform hover:scale-110 hover:shadow-lg">
                            <img src={item.icon} alt={item.name} className="w-full h-full object-cover object-center"/>
                        </div>
                        <div
                            className="absolute left-20 top-1/2 transform -translate-y-1/2 z-50 bg-black text-white text-sm font-bold px-2 py-1 rounded-md opacity-0 group-hover:opacity-100 transition-opacity duration-300 tooltip-arrow pointer-events-none group-hover:pointer-events-auto"
                            style={{width: "max-content"}}
                        >
                            {item.name}
                        </div>
                    </Link>
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