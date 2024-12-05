import * as React from "react";
import {useCreateChannel} from "./hooks/UseCreateChannel";
import {CreateChannelsState} from "./hooks/states/createChannelsState";
import {UseCreateChannelHandler} from "./hooks/handler/UseCreateChannelHandler";
import {Navigate} from "react-router-dom";
import {SearchBar} from "../components/SearchBar";
import {ToggleCreateChannelsView} from "./components/ToggleCreateChannelView";

export function CreateChannelsView(): React.JSX.Element {
    const [state, handler]: [CreateChannelsState, UseCreateChannelHandler] = useCreateChannel()

    const view = ((state: CreateChannelsState) =>{
        switch (state.tag) {
            case "redirecting":
                return <Navigate to={"/channels"}/>
            case "error":
                return <div>{state.message}</div>
        }
    })
    return (
        <div>
            <h1>Create Channel</h1>
            <SearchBar
                value={state.input.name}
                onChange={handler.onNameChange}
                placeholder={"Channel Name"}
                className={"bg-gray-700 text-white p-2 rounded"}/>
            <ToggleCreateChannelsView onGenerate={handler.onVisibilityChange}></ToggleCreateChannelsView>
        </div>
    )
}