import * as React from 'react';
import {reduce, useFindChannels} from "./hooks/UseFindChannels";
import {FindChannelsService, makeDefaultFindChannelService} from "../../service/findChannels/FindChannelService";
import {useLocation} from "react-router-dom";
import {State} from "./hooks/states/FindChannelsState";
import {SearchBar} from "./components/shared/SearchBar";

const DEBOUNCE_DELAY = 500;

export function FindChannels(
    service: FindChannelsService = makeDefaultFindChannelService()
): React.JSX.Element {
    const location = useLocation();
    const [state, handler] = useFindChannels()

    const view = ((state: State) => {
        switch (state.tag) {
            case "navigating":
                return <FindChannelsView handler={handler}/>
            case "searching":
                return <FindChannelsSearchingView handler={handler}/>
            case "fetchingMore":
                return <FindChannelsFetchingMoreView handler={handler}/>
            case "error":
                return <FindChannelsErrorView handler={handler}/>
            case "joining":
                return <FindChannelsJoiningView handler={handler}/>
        }
    })

    return (
        <div>
            <h1>Find Channels</h1>
            <SearchBar value={} onChange={}
            {view(state)}
        </div>
    )

}






