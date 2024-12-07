import * as React from "react"
import {createRoot} from "react-dom/client"
import '../../public/index.css'
import '@fortawesome/fontawesome-free/css/all.min.css';

import {
    createBrowserRouter,
    Navigate, Outlet,
    RouterProvider,
} from "react-router-dom"
import {LoginView} from "../view/registration/login/LoginView";
import {RegisterView} from "../view/registration/register/RegisterView";
import {LoginServiceProvider} from "../service/registration/login/LoginServiceProvider";
import {RegisterServiceProvider} from "../service/registration/register/RegisterServiceProvider";
import {ChannelServiceProvider} from "../service/channel/ChannelServiceProvider";
import {AuthValidator} from "../view/session/authValidator";
import {FindChannelsView} from "../view/findChannels/FindChannelsView";
import {AboutView} from "../view/about/AboutView";
import {CreateChannelsView} from "../view/createChannels/createChannelsView";
import {ChannelsView} from "../view/channels/ChannelsView";
import {ChannelsServicesProvider} from "../service/channels/ChannelsServicesProvider";
import {CreateChannelInvitationView} from "../view/createChannelInvitation/CreateChannelInvitation";
import {SseCommunicationServiceProvider} from "../service/sse/SseCommunicationServiceProvider";
import {
    RegisterCommunicationServiceProvider
} from "../service/registration/communication/RegisterCommunicationProvider";
import {ChannelView} from "../view/channel/ChannelView";
import {FindChannelsServiceProvider} from "../service/findChannels/FindChannelsServiceProvider";
import {
    CreateChannelInvitationServiceProvider
} from "../service/createChannelInvitation/CreateChannelInvitationServiceProvider";
import {CreateChannelServiceProvider} from "../service/createChannels/createChannelsServiceProvider";
import {ImagePickerProvider} from "../view/components/ImagePicker/ImagePickerProvider";
import ImagePicker from "../view/components/ImagePicker/ImagePicker";

const router = createBrowserRouter(
    [
        {
            "path": "/",
            "element": <Navigate to="/channels" replace/>,
        },
        {
            "path": "/register",
            "element":
                <RegisterCommunicationServiceProvider>
                    <Navigate to={"/register/login"} replace/>
                    <Outlet/>
                </RegisterCommunicationServiceProvider>,
            "children": [
                {
                    "path": "/register/login",
                    "element":
                        <LoginServiceProvider>
                            <LoginView/>
                        </LoginServiceProvider>,
                },
                {
                    "path": "/register/singIn",
                    "element":
                        <RegisterServiceProvider>
                            <RegisterView/>
                        </RegisterServiceProvider>,
                },
            ]
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
                    children: [
                        {
                            "path": "/channels/:id/createInvitation",
                            "element":
                                <CreateChannelInvitationServiceProvider>
                                    <CreateChannelInvitationView/>
                                </CreateChannelInvitationServiceProvider>
                        ,
                        },
                    ]
                },
                {
                    "path": "/channels/findChannels",
                    "element":
                        <FindChannelsServiceProvider>
                            <FindChannelsView/>
                        </FindChannelsServiceProvider>,
                },
                {
                    "path": "/channels/createChannel",
                    "element":
                        <CreateChannelServiceProvider>
                            <ImagePickerProvider>
                                <CreateChannelsView/>
                                <ImagePicker/>
                            </ImagePickerProvider>

                        </CreateChannelServiceProvider>,
                },
            ]
                },
        {
            "path": "/about",
            "element": <AboutView />,
        },
    ]
)

export function app() {
    createRoot(document.getElementById('container')).render(
        <RouterProvider router={router}/>
    )
}
