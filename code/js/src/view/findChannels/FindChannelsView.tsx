import * as React from 'react';
import {useFindChannels} from "./hooks/UseFindChannels";
import {Navigate} from "react-router-dom";
import {FindChannelState} from "./hooks/states/FindChannelsState";
import {SearchBar} from "./components/shared/SearchBar";
import {FindChannelsFetchingMoreView} from "./components/FindChannelsFetchingMoreView";
import {urlBuilder} from "../../service/utils/UrlBuilder";
import {FindChannelsErrorView} from "./components/FindChannelsErrorView";
import {FindChannelsNavigatingView} from "./components/FindChannelsNavigatingView";
import {FindChannelsLoadingView} from "./components/FindChannelsLoadingView";
import {UseFindChannelsHandler} from "./hooks/handler/UseFindChannelsHandler";

export function FindChannelsView(): React.JSX.Element {
    const [state, handler]: [FindChannelState, UseFindChannelsHandler] = useFindChannels();

    if(state.tag === "redirect") {
        const channelId = state.channelId
        window.history.replaceState(null, "", urlBuilder("/channels/" + channelId));
    }

    if (state.tag === "navigating" && state.channels.length === 0 && state.searchBar === "") {
        handler.onFetchMore()
    }

    if (state.tag === "searching") {
        handler.onFetch()
    }

    const view  = ((state: FindChannelState) => {
        switch (state.tag) {
            case "navigating":
                return <FindChannelsNavigatingView channels={state.channels} onClick={handler.onJoin}/>
            case "searching":
                return <FindChannelsLoadingView/>
            case "fetchingMore":
                return <FindChannelsFetchingMoreView/>
            case "error":
                return <FindChannelsErrorView error={state.error} onClose={handler.onErrorClose}/>
            case "joining":
                return <FindChannelsLoadingView/>
        }
    })
    return (
        <div>
            <header className="bg-gray-800 p-4 flex items-center">
                <h1 className="text-xl font-bold">Find Channels</h1>
                <SearchBar value={state.searchBar} onChange={handler.onSearchChange} className={"bg-gray-700 text-white p-2 rounded ml-auto"}/>
            </header>
            <main className={"p-8"}>
                {state.tag === "navigating" && state.searchBar === "" && (
                    <section className="text-center mb-8">
                        <h1 className="text-4xl font-bold">FIND YOUR CHANNEL</h1>
                        <p className="text-gray-400">From gaming, to music, to learning, there's a place for you.</p>
                    </section>
                )}
                {view(state)}
            </main>
        </div>
    )
}






