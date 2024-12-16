import * as React from "react"
import {createRoot} from "react-dom/client"
import '../../public/index.css'
import '@fortawesome/fontawesome-free/css/all.min.css';

import {createBrowserRouter, Navigate, Outlet, RouterProvider} from "react-router-dom"
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
import {RegisterCommunicationServiceProvider} from "../service/registration/communication/RegisterCommunicationProvider";
import {ChannelView} from "../view/channel/ChannelView";
import {FindChannelsServiceProvider} from "../service/findChannels/FindChannelsServiceProvider";
import {CreateChannelInvitationServiceProvider} from "../service/createChannelInvitation/CreateChannelInvitationServiceProvider";
import {CreateChannelServiceProvider} from "../service/createChannels/CreateChannelsServiceProvider";
import {ImagePickerProvider} from "../view/components/ImagePicker/ImagePickerProvider";
import ImagePicker from "../view/components/ImagePicker/ImagePicker";
import {CreateUserInvitationView} from "../view/createUserInvitation/CreateUserInvitationView";
import {CreateUserInvitationServiceProvider} from "../service/createUserInvitation/CreateUserInvitationServiceProvider";
import {EditChannelServiceProvider} from "../service/editChannel/EditChannelServiceProvider";
import EditChannelView from "../view/editChannel/EditChannelView";
import {JoinChannelServiceProvider} from "../service/joinChannel/JoinChannelServiceProvider";
import {JoinChannelView} from "../view/joinChannels/JoinChannelView";
import {ChannelCommunicationProvider} from "../service/channel/communication/ChannelCommunicationProvider";
import {ChannelsCommunicationProvider} from "../service/channels/communication/ChannelsCommunicationProvider";

/**
 * @description The main router of the application.
 */
const router = createBrowserRouter(
    [
        {
            "path": "/",
            "element": <Navigate to="/channels/findChannels" replace/>,
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
                        <ChannelsCommunicationProvider>
                            <ChannelsView/>
                        </ChannelsCommunicationProvider>
                    </ChannelsServicesProvider>
                </AuthValidator>,
            "children": [
                {
                    "path": "/channels/:id",
                    "element":
                        <SseCommunicationServiceProvider>
                            <ChannelServiceProvider>
                                <ChannelCommunicationProvider>
                                    <ChannelView/>
                                </ChannelCommunicationProvider>
                            </ChannelServiceProvider>
                        </SseCommunicationServiceProvider>,
                    children: [
                        {
                            "path": "/channels/:id/createInvitation",
                            "element":
                                <CreateChannelInvitationServiceProvider>
                                    <CreateChannelInvitationView/>
                                </CreateChannelInvitationServiceProvider>
                        },
                        {
                            "path": "/channels/:id/edit",
                            "element":
                                <EditChannelServiceProvider>
                                    <ImagePickerProvider>
                                        <EditChannelView/>
                                        <ImagePicker/>
                                    </ImagePickerProvider>
                                </EditChannelServiceProvider>
                        }
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
                {
                    "path": "/channels/createUserInvitation",
                    "element":
                        <CreateUserInvitationServiceProvider>
                            <CreateUserInvitationView/>
                        </CreateUserInvitationServiceProvider>,

                },
                {
                    "path": "/channels/joinChannel",
                    "element":
                        <JoinChannelServiceProvider>
                            <JoinChannelView/>
                        </JoinChannelServiceProvider>,
                },
                {
                    "path": "/channels/about",
                    "element": <AboutView />,
                },
            ]
        },
    ]
)

export function app() {
    createRoot(document.getElementById('container')).render(
        <RouterProvider router={router}/>
    )
}
