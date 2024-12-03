import React from "react";
import {CreateChannelInvitationState} from "./hooks/states/CreateChannelInvitationState";
import {UseCreateChannelInvitationHandler} from "./hooks/handler/UseCreateChannelInvitationHandler";
import {useCreateChannelInvitation} from "./hooks/useCreateChannelInvitation";
import {CreateChannelInvitationEditingView} from "./components/CreateChannelInvitationEditingView";


export function CreateChannelInvitationView(): React.JSX.Element {
    const [state, handler]: [CreateChannelInvitationState, UseCreateChannelInvitationHandler] = useCreateChannelInvitation();



    const view = ((state: CreateChannelInvitationState) => {
        switch (state.tag) {
            case "editingInvitationToken":
                return <CreateChannelInvitationEditingView onGenerate={handler.onCreate}></CreateChannelInvitationEditingView>
            case "creating":
                return <div>Creating...</div>
            case "showingInvitationToken":
                return <div>Showing invitation token...${state.invitationToken}</div>
            case "error":
                return <div>Error: ${state.error}</div>
            case "closing":
                return <div>Closing...</div>
        }
    })

    return (
        <div className="flex items-center justify-center min-h-screen">
            <div className="bg-gray-800 text-white p-6 rounded-lg shadow-lg w-96">
                <div className="flex justify-between items-center mb-4">
                    <h2 className="text-lg font-semibold">Channel invite code settings</h2>
                    <button className="text-gray-400 hover:text-gray-200">
                        <i className="fas fa-times"></i>
                    </button>
                </div>
                {view(state)}
            </div>
        </div>
    )
}