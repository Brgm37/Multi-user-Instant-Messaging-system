import * as React from 'react';
import { ChangeEvent, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { PublicChannelsContext } from "./ChannelsProvider";
import { reduce } from "../../service/findChannels/FindChannelsReducer";

const DEBOUNCE_DELAY = 500;

export function FindChannels(): React.JSX.Element {
    const { channels, setChannels } = React.useContext(PublicChannelsContext);
    const [state, dispatch] = React.useReducer(reduce, {
        tag: "navigating",
        searchBar: "",
        channels: []
    });

    
}