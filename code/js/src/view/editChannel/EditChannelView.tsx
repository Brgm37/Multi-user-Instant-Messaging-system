import * as React from 'react';
import {useEditChannel} from "./hooks/UseEditChannel";
import {Navigate} from "react-router-dom";
import {EditChannelState} from "./hooks/states/EditChannelState";

export default function (): React.JSX.Element {
    const [state, handler] = useEditChannel()
    if (state.tag === "idle") {
        handler.loadChannel()
        return (
            <div className="flex items-center justify-center h-screen">
                <h1>Loading...</h1>
            </div>
        )
    }
    if (state.tag === "redirect") {
        return <Navigate to={"/channels/" + state.cId}/>
    }
    const switchState = (state: EditChannelState) => {
        switch (state.tag) {
            case "idle":
                return <div className="flex items-center justify-center h-screen">
                    <h1>Loading...</h1>
                </div>
            case "redirect":
                return <Navigate to={"/channels/" + state.cId}/>
            case "error":
                return <div className="flex items-center justify-center h-screen">
                    <h1>Error</h1>
                </div>
            case "editing":
                return <div className="flex items-center justify-center h-screen">
                    <h1>Editing...</h1>
                </div>
            default:
                return <div className="flex items-center justify-center h-screen">
                    <h1>Unknown state</h1>
                </div>
        }
    }

    return (
        <div>
            {switchState(state)}
        </div>
    )
}