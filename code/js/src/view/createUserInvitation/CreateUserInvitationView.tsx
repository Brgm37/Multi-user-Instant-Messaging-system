import React from "react";

import {CreateUserInvitationState} from "./hooks/states/CreateUserInvitationState";
import {UseCreateUserInvitationHandler} from "./hooks/handler/UseCreateUserInvitationHandler";
import {useCreateUserInvitation} from "./hooks/UseCreateUserInvitation";
import {CreateUserInvitationEditingView} from "./components/CreateUserInvitationEditingView";
import {FaClipboard} from "react-icons/fa";
import {Navigate} from "react-router-dom";

/**
 * The user invitation view.
 */
export function CreateUserInvitationView(): React.JSX.Element {
    const [state, handler]: [CreateUserInvitationState, UseCreateUserInvitationHandler]= useCreateUserInvitation();

    const view = ((state: CreateUserInvitationState) => {
        switch (state.tag) {
            case "editingInvitationCode":
                return <CreateUserInvitationEditingView onGenerate={handler.onCreate}></CreateUserInvitationEditingView>
            case "creating":
                return <div>Creating...</div>
            case "showingInvitationCode":
                return (
                    <div className="flex flex-col items-center p-6 bg-gray-800 rounded-lg shadow-lg max-w-sm mx-auto">
                        <div className="text-xl font-bold text-white mb-4 text-center">
                            {state.invitationCode}
                        </div>
                        <button
                            onClick={() => {
                                navigator.clipboard.writeText(state.invitationCode)
                                    .then(() => alert('Invitation code copied to clipboard!'));
                            }}
                            className="mt-2 p-3 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition duration-200 ease-in-out w-full flex justify-center items-center"
                        >
                            <FaClipboard className="h-6 w-6" aria-hidden="true"/>
                        </button>
                    </div>
                )
            case "error":
                return <div>Error: ${state.message}</div>
            case "closing":
                return <Navigate to={"/channels/findChannels"}/>
        }
    })

    return (
        <div className="flex items-center justify-center min-h-screen">
            <div className="bg-gray-800 text-white p-6 rounded-lg shadow-lg w-96">
                <div className="flex justify-between items-center mb-4">
                    <h2 className="text-lg font-semibold">User invite code settings</h2>
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