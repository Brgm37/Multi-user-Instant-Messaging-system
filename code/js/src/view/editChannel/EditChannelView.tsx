import * as React from 'react';
import {useEditChannel} from "./hooks/UseEditChannel";
import {Navigate, useNavigate} from "react-router-dom";
import {EditChannelState} from "./hooks/states/EditChannelState";
import {EditChannelErrorView} from "./components/EditChannelErrorView";
import {EditChannelLoadingView} from "./components/EditChannelLoadingView";
import {EditChannelEditingVIew} from "./components/EditChannelEditingVIew";
import {ImagePickerProvider} from "../components/ImagePicker/ImagePickerProvider";

export default function (): React.JSX.Element {
    const [state, handler] = useEditChannel()
    const navigate = useNavigate()
    if (state.tag === "idle") {
        handler.loadChannel()
        return (<EditChannelLoadingView/>)
    }
    if (state.tag === "redirect") {
        return <Navigate to={"/channels/" + state.cId}/>
    }
    const switchState = (state: EditChannelState) => {
        switch (state.tag) {
            case "error":
                return <div className="flex items-center justify-center h-screen">
                    <EditChannelErrorView error={state.message} goBack={handler.goBack}/>
                </div>
            case "editing":
                return (
                    <EditChannelEditingVIew
                        initDescription={state.channel.description}
                        initVisibility={state.channel.visibility}
                        handleSubmit={handler.onSubmit}
                    />
                )
            default:
                return <EditChannelLoadingView/>
        }
    }

    const goBack = () => {
        navigate(-1)
        window.location.reload()
    }

    return (
        <div className="flex items-center justify-center min-h-screen z-50">
            <div className="bg-gray-900 text-white p-6 rounded-lg shadow-lg w-96">
                <div className="flex justify-between items-center mb-4">
                    <h2 className="text-lg font-semibold">Edit Channel</h2>
                    <button
                        className="text-gray-400 hover:text-gray-200"
                        onClick={goBack}
                    >
                        <i className="fas fa-times"></i>
                    </button>
                </div>
                {switchState(state)}
            </div>
        </div>
    )
}