import * as React from "react"
import {createRoot} from "react-dom/client"

import {
    createBrowserRouter,
    Navigate,
    RouterProvider,
} from "react-router-dom"
import {LoginView} from "../view/registration/login/LoginView";
import {SignInView} from "../view/registration/signIn/SignInView";
import {LoginServiceProvider} from "../service/registration/login/LoginServiceProvider";
import {SignInServiceProvider} from "../service/registration/signIn/SignInServiceProvider";
import ChannelView from "../view/channel/ChannelView";
import {ChannelServiceProvider} from "../service/channel/ChannelServiceProvider";
import {AuthValidator} from "../view/session/authValidator";
import {FindChannelsView} from "../view/findChannels/FindChannelsView";
import {FindChannelsMockServiceProvider} from "../service/findChannels/mock/FindChannelsMockServiceProvider";
import {ChannelsView} from "../view/channels/ChannelsView";
import {ChannelsServiceProviderMock} from "../service/channels/ChannelsServiceProviderMock";

const router = createBrowserRouter(
    [
        {
            "path": "/",
            "element": <Navigate to="/login" replace/>,
        },
        {
            "path": "/login",
            "element":
                <LoginServiceProvider>
                    <LoginView/>
                </LoginServiceProvider>,
        },
        {
            "path": "/signIn",
            "element":
                <SignInServiceProvider>
                    <SignInView/>
                </SignInServiceProvider>,
        },
        {
            "path": "/channels",
            "element":
                <AuthValidator>
                    <ChannelsServiceProviderMock>
                        <ChannelsView/>
                    </ChannelsServiceProviderMock>
                </AuthValidator>,
            "children": [
                {
                    "path": ":id",
                    "element":
                        <ChannelServiceProvider>
                            <ChannelView/>
                        </ChannelServiceProvider>
                }
            ]
        },
        {
            "path": "/findChannels",
            "element":
                <FindChannelsMockServiceProvider>
                    <FindChannelsView/>
                </FindChannelsMockServiceProvider>,
        }
    ]
)

export function app() {
    createRoot(document.getElementById('container')).render(
        <RouterProvider router={router}/>
    )
}
