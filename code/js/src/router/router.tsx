import * as React from "react"
import {createRoot} from "react-dom/client"
import '../../public/index.css'
import '@fortawesome/fontawesome-free/css/all.min.css';

import {
    createBrowserRouter,
    Navigate,
    RouterProvider,
} from "react-router-dom"
import {LoginView} from "../view/registration/login/LoginView";
import {RegisterView} from "../view/registration/register/RegisterView";
import {LoginServiceProvider} from "../service/registration/login/LoginServiceProvider";
import {RegisterServiceProvider} from "../service/registration/register/RegisterServiceProvider";
import ChannelView from "../view/channel/ChannelView";
import {ChannelServiceProvider} from "../service/channel/ChannelServiceProvider";
import {AuthValidator} from "../view/session/authValidator";
import {FindChannelsView} from "../view/findChannels/FindChannelsView";
import {FindChannelsMockServiceProvider} from "../service/findChannels/mock/FindChannelsMockServiceProvider";
import {ChannelsView} from "../view/channels/ChannelsView";
import {ChannelsServicesProvider} from "../service/channels/ChannelsServicesProvider";
import {
    CreateChannelInvitationMockServiceProvider
} from "../service/createChannelInvitation/mock/CreateChannelInvitationMockServiceProvider";
import {CreateChannelInvitationView} from "../view/createChannelInvitation/CreateChannelInvitation";
import {SseCommunicationServiceProvider} from "../service/sse/SseCommunicationServiceProvider";

const router = createBrowserRouter(
    [
        {
            "path": "/",
            "element": <Navigate to="/channels" replace/>,
        },
        {
            "path": "/login",
            "element":
                <LoginServiceProvider>
                    <LoginView/>
                </LoginServiceProvider>,
        },
        {
            "path": "/register",
            "element":
                <RegisterServiceProvider>
                    <RegisterView/>
                </RegisterServiceProvider>,
        },
        {
            "path": "/channels",
            "element":
                <AuthValidator>
                    <ChannelsServicesProvider>
                        <ChannelsView/>
                    </ChannelsServicesProvider>
                </AuthValidator>,
            "children": [
                {
                    "path": "/channels/:id",
                    "element":
                    <SseCommunicationServiceProvider>
                        <ChannelServiceProvider>
                            <ChannelView/>
                        </ChannelServiceProvider>
                    </SseCommunicationServiceProvider>,
                }
            ]
        },
        {
            "path": "/findChannels",
            "element":
                <FindChannelsMockServiceProvider>
                    <FindChannelsView/>
                </FindChannelsMockServiceProvider>,
        },
        {
            "path": "/dummy",
            "element":
            <CreateChannelInvitationMockServiceProvider>
                <CreateChannelInvitationView/>
            </CreateChannelInvitationMockServiceProvider>,
        }
    ]
)

export function app() {
    createRoot(document.getElementById('container')).render(
        <RouterProvider router={router}/>
    )
}
