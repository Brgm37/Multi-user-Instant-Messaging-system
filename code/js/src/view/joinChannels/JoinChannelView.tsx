import React, {useContext, useEffect} from "react";
import {Navigate, useNavigate} from "react-router-dom";
import {useJoinChannel} from "./hooks/UseJoinChannel";
import {UseJoinChannelHandler} from "./hooks/handler/UseJoinChannelHandler";
import {JoinChannelState} from "./hooks/states/JoinChannelState";
import {ChannelsCommunicationContext} from "../../service/channels/communication/ChannelsCommunicationContext";

/**
 * The join channel view.
 */
export function JoinChannelView(): React.JSX.Element {
    const [state, handler]: [JoinChannelState, UseJoinChannelHandler] = useJoinChannel()
    const [token, setToken] = React.useState("")
    const communication = useContext(ChannelsCommunicationContext)
    const navigate = useNavigate()

    useEffect(() => {
        if (state.tag === "UseJoinSuccess") {
            communication.toggleReload()
            navigate("/channels/" + state.id)
        }
    }, [state]);

    const view = ((state: JoinChannelState) => {
        switch (state.tag) {
            case "UseJoin":
                return (
                    <div>
                        <div className="mb-4">
                            <label className="block text-sm font-medium mb-1">CHANNEL INVITATION TOKEN</label>
                            <input
                                value={token}
                                onChange={(e) => setToken(e.target.value)}
                                placeholder={"Token"}
                                className={"w-full p-2 bg-gray-700 rounded border border-gray-600 focus:outline-none focus:border-blue-500"}
                            />
                        </div>
                        <div className="mb-4">
                            <button
                                onClick={() => handler.onJoin(token)}
                                className="w-full p-2 bg-blue-600 rounded hover:bg-blue-700 focus:outline-none focus:bg-blue-700"
                            >Join
                            </button>
                        </div>

                    </div>
                )
            case "UseJoinError":
                return <div>Error: {state.message}</div>
            case "UseJoinClose":
                return <Navigate to={"/channels/findChannels"} />
        }
    })
    return (
        <div className="flex items-center justify-center min-h-screen">
            <div className="bg-gray-800 text-white p-6 rounded-lg shadow-lg w-96">
                <div className="flex justify-between items-center mb-4">
                    <h2 className="text-lg font-semibold">Join Channel</h2>
                    <button
                        className="text-gray-400 hover:text-gray-200"
                        onClick={handler.onClose}
                    >
                        <i className="fas fa-times"></i>
                    </button>
                </div>
                {view(state)}
            </div>
        </div>
    )
}