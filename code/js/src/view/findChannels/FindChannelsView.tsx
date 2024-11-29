import * as React from 'react';
import {reduce, useFindChannels, UseFindChannelsHandler} from "./hooks/UseFindChannels";
import {FindChannelsService, makeDefaultFindChannelService} from "../../service/findChannels/FindChannelService";
import {Navigate, useLocation} from "react-router-dom";
import {FindChannelState} from "./hooks/states/FindChannelsState";
import {SearchBar} from "./components/shared/SearchBar";
import {FindChannelsFetchingMoreView} from "./components/FindChannelsFetchingMoreView";
import * as url from "node:url";
import {urlBuilder} from "../../service/utils/UrlBuilder";

const DEBOUNCE_DELAY = 500;

export function FindChannelsView(
    service: FindChannelsService = makeDefaultFindChannelService()
): React.JSX.Element {
    const location = useLocation();
    const [state, handler]: [FindChannelState, UseFindChannelsHandler] = useFindChannels();

    if(state.tag === "redirect") {
        const channelId = state.channelId
        return <Navigate to={urlBuilder("/channels/" + channelId)} replace={true}></Navigate>
    }

    if (state.tag === "error") {
        return
    }

    const view  = ((state: FindChannelState) => {
        switch (state.tag) {
            case "navigating":
                return <FindChannelsFetchingMoreView/>
            case "searching":
                return <FindChannelsFetchingMoreView/>
            case "fetchingMore":
                return <FindChannelsFetchingMoreView/>
            case "error":
                return <FindChannelsFetchingMoreView/>
            case "joining":
                return <FindChannelsFetchingMoreView/>
            default:
                return <FindChannelsFetchingMoreView/>
        }
    })

    return (
        <div>
            <h1>Find Channels</h1>
            <SearchBar value={state.searchBar} onChange={handler.onSearchChange}/>
            { view(state) }
        </div>
    )

}






