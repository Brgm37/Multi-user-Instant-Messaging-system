import * as React from 'react';
import {useFindChannels} from "./hooks/UseFindChannels";
import {Navigate} from "react-router-dom";
import {FindChannelState} from "./hooks/states/FindChannelsState";
import {SearchBar} from "../components/SearchBar";
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
        return <Navigate to={urlBuilder("/channels/" + channelId)} replace={true}></Navigate>
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
                return <FindChannelsNavigatingView channels={state.channels}/>
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
            <h1>Find Channels</h1>
            <SearchBar value={state.searchBar} onChange={handler.onSearchChange} isSearching={false}/>
            { view(state) }
        </div>
    )

}






