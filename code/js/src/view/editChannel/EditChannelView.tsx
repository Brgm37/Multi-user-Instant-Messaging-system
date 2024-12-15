import * as React from 'react';
import {useEditChannel} from "./hooks/UseEditChannel";
import {useNavigate} from "react-router-dom";
import {EditChannelState} from "./hooks/states/EditChannelState";
import {EditChannelErrorView} from "./components/EditChannelErrorView";
import {EditChannelLoadingView} from "./components/EditChannelLoadingView";
import {EditChannelEditingVIew} from "./components/EditChannelEditingVIew";
import {ChannelCommunicationContext} from "../../service/channel/communication/ChannelCommunicationContext";
import {useEffect} from "react";
import {ChannelsCommunicationContext} from "../../service/channels/communication/ChannelsCommunicationContext";

/**
 * The edit channel view.
 */
export default function (): React.JSX.Element {
    const [state, handler] = useEditChannel()
    const navigate = useNavigate()
    const channelCommunication = React.useContext(ChannelCommunicationContext)
    const channelsCommunication = React.useContext(ChannelsCommunicationContext)

    useEffect(() => {
        if (state.tag === "redirect") {
            channelCommunication.toggleReload();
            channelsCommunication.toggleReload();
            navigate("/channels/" + state.cId);
        }
    }, [state]);

    if (state.tag === "idle") {
        handler.loadChannel()
        return (<EditChannelLoadingView/>)
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