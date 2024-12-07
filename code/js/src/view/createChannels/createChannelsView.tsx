import * as React from "react";
import {useCreateChannel} from "./hooks/UseCreateChannel";
import {CreateChannelsState} from "./hooks/states/createChannelsState";
import {UseCreateChannelHandler} from "./hooks/handler/UseCreateChannelHandler";
import {Navigate} from "react-router-dom";
import {CreateChannelsBaseView} from "./components/CreateChannelsBaseView";
import {useContext} from "react";
import {useImagePicker} from "../components/ImagePicker/ImagePickerProvider";
import {InputBar} from "../components/InputBar";

export function CreateChannelsView(): React.JSX.Element {
    const [state, handler]: [CreateChannelsState, UseCreateChannelHandler] = useCreateChannel()

    const view = ((state: CreateChannelsState) =>{
        switch (state.tag) {
            case "redirecting": {
                window.location.reload()
                return <Navigate to={"/channels"}/>
            }
            case "error":
                return <div>{state.message}</div>
            case "submitting":
                return <div>Submitting...</div>
            case "editing":
                return <CreateChannelsBaseView handler={handler} state={state}/>
        }
    })
    return (
        <div>
            <header className="bg-gray-900 p-4 flex items-center w-full">
                <h1 className="text-xl font-bold">Create a New Channel</h1>
                <div className={"p-5 ml-auto"}></div>
            </header>
            <div className="container mx-auto p-8">
                {view(state)}
            </div>
        </div>
    )
}