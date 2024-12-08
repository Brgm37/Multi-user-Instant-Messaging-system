import React from "react";
import {CreateChannelInvitationState} from "./hooks/states/CreateChannelInvitationState";
import {UseCreateChannelInvitationHandler} from "./hooks/handler/UseCreateChannelInvitationHandler";
import {useCreateChannelInvitation} from "./hooks/useCreateChannelInvitation";
import {CreateChannelInvitationEditingView} from "./components/CreateChannelInvitationEditingView";
import {Navigate} from "react-router-dom";
import {FaClipboard} from "react-icons/fa";

/**
 * The create channel invitation view.
 */
export function CreateChannelInvitationView(): React.JSX.Element {
    const [state, handler]: [CreateChannelInvitationState, UseCreateChannelInvitationHandler] = useCreateChannelInvitation();

    if(state.tag === "closing") {
        return <Navigate to={"/channels/" + state.cId} replace={true}/>
    }

    const view = ((state: CreateChannelInvitationState) => {
        switch (state.tag) {
            case "editingInvitationToken":
                return <CreateChannelInvitationEditingView handler={handler}/>
            case "creating":
                return <div>Creating...</div>
            case "showingInvitationToken":
                return (
                    <div className="flex flex-col items-center p-6 bg-gray-800 rounded-lg shadow-lg max-w-sm mx-auto">
                        <div className="text-xl font-bold text-white mb-4 text-center">
                            {state.invitationToken}
                        </div>
                        <button
                            onClick={() => {
                                navigator.clipboard.writeText(state.invitationToken)
                                    .then(() => alert('Invitation token copied to clipboard!'));
                            }}
                            className="mt-2 p-3 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition duration-200 ease-in-out w-full flex justify-center items-center"
                        >
                            <FaClipboard className="h-6 w-6" aria-hidden="true"/>
                        </button>
                    </div>
                )
            case "error":
                return <div>Error: ${state.error}</div>
            case "closing":
                return <div>Closing...</div>
        }
    })

    return (
        <div className="flex items-center justify-center min-h-screen z-50">
            <div className="bg-gray-900 text-white p-6 rounded-lg shadow-lg w-96">
                <div className="flex justify-between items-center mb-4">
                    <h2 className="text-lg font-semibold">Channel invite code settings</h2>
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