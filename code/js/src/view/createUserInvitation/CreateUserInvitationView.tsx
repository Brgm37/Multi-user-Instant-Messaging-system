import React from "react";
import {CreateChannelInvitationState} from "../createChannelInvitation/hooks/states/CreateChannelInvitationState";
import {
    CreateChannelInvitationEditingView
} from "../createChannelInvitation/components/CreateChannelInvitationEditingView";
import {CreateUserInvitationState} from "./hooks/states/CreateUserInvitationState";
import {UseCreateUserInvitationHandler} from "./hooks/handler/UseCreateUserInvitationHandler";
import {useCreateUserInvitation} from "./hooks/UseCreateUserInvitation";


export function CreateUserInvitationView(): React.JSX.Element {
    const [state, handler]: [CreateUserInvitationState, UseCreateUserInvitationHandler]= useCreateUserInvitation();

    const view = ((state: CreateUserInvitationState) => {
        switch (state.tag) {
            case "editingInvitationCode":
                return <CreateChannelInvitationEditingView onGenerate={handler.onCreate}></CreateChannelInvitationEditingView>
            case "creating":
                return <div>Creating...</div>
            case "showingInvitationCode":
                return <div>Showing invitation code...${state.invitationCode}</div>
            case "error":
                return <div>Error: ${state.message}</div>
            case "closing":
                return <div>Closing...</div>
        }
    })

    return (
        <div className="flex items-center justify-center min-h-screen">
            <div className="bg-gray-800 text-white p-6 rounded-lg shadow-lg w-96">
                <div className="flex justify-between items-center mb-4">
                    <h2 className="text-lg font-semibold">User invite code settings</h2>
                    <button className="text-gray-400 hover:text-gray-200">
                        <i className="fas fa-times"></i>
                    </button>
                </div>
                {view(state)}
            </div>
        </div>
    )
}