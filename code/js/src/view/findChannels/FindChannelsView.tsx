import * as React from 'react';
import {reduce, useFindChannels, UseFindChannelsHandler} from "./hooks/UseFindChannels";
import {FindChannelsService, makeDefaultFindChannelService} from "../../service/findChannels/FindChannelService";
import {Navigate, useLocation} from "react-router-dom";
import {FindChannelState} from "./hooks/states/FindChannelsState";
import {SearchBar} from "./components/shared/SearchBar";
import {FindChannelsFetchingMoreView} from "./components/FindChannelsFetchingMoreView";
import * as url from "node:url";
import {urlBuilder} from "../../service/utils/UrlBuilder";
import {FindChannelsErrorView} from "./components/FindChannelsErrorView";
import {FindChannelsNavigatingView} from "./components/FindChannelsNavigatingView";
import {FindChannelsLoadingView} from "./components/FindChannelsLoadingView";

const DEBOUNCE_DELAY = 500;

export function FindChannelsView(
    {service}: {service: FindChannelsService} = {service: makeDefaultFindChannelService()}
): React.JSX.Element {
    const location = useLocation();
    const [state, handler]: [FindChannelState, UseFindChannelsHandler] = useFindChannels(service);

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
                return <FindChannelsNavigatingView/>
            case "searching":
                return <FindChannelsLoadingView/>
            case "fetchingMore":
                return <FindChannelsFetchingMoreView/>
            case "error":
                return <FindChannelsErrorView error={state.error}/>
            case "joining":
                return <FindChannelsLoadingView/>
            default:
                return <FindChannelsNavigatingView/>
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






