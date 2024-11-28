import * as React from 'react';
import { ChangeEvent, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { reduce } from "./hooks/UseFindChannels";

const DEBOUNCE_DELAY = 500;

// export function FindChannels(): React.JSX.Element {
//     const [state, dispatch] = React.useReducer(reduce, {
//         tag: "navigating",
//         searchBar: "",
//         channels: []
//     });
//
//
