import * as React from "react";
import {useCreateChannel} from "./hooks/UseCreateChannel";
import {CreateChannelsState} from "./hooks/states/createChannelsState";
import {UseCreateChannelHandler} from "./hooks/handler/UseCreateChannelHandler";
import {Navigate} from "react-router-dom";
import {CreateChannelsBaseView} from "./components/CreateChannelsBaseView";

export function CreateChannelsView(): React.JSX.Element {
    const [state, handler]: [CreateChannelsState, UseCreateChannelHandler] = useCreateChannel()

    const view = ((state: CreateChannelsState) =>{
        switch (state.tag) {
            case "redirecting":
                return <Navigate to={"/channels"}/>
            case "error":
                return <div>{state.message}</div>
            case "submitting":
                return <div>Submitting...</div>
            case "editing":
                return <CreateChannelsBaseView handler={handler} state={state} onGenerate={handler.onSubmit}/>
        }
    })
    return (
        <div>
            <h1>Create Channel</h1>
            {view(state)}
        </div>
    )
}