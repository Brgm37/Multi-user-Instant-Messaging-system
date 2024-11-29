import * as React from 'react';
import { reduce } from "./hooks/UseFindChannels";
import {FindChannelsService, makeDefaultFindChannelService} from "../../service/findChannels/FindChannelService";

const DEBOUNCE_DELAY = 500;

export function FindChannels(
    service: FindChannelsService = makeDefaultFindChannelService()
): React.JSX.Element {
    const [state, dispatch] = React.useReducer(reduce, {
        tag: "navigating",
        searchBar: "",
        channels: []
    })



