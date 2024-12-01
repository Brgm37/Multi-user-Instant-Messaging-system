import * as React from "react"
import {createRoot} from "react-dom/client"

import {
    createBrowserRouter,
    Link, Navigate,
    Outlet,
    RouterProvider,
    useParams,
} from "react-router-dom"
import {LoginView} from "../view/registration/login/LoginView";
import {SignInView} from "../view/registration/signIn/SignInView";
import { DevView } from "../view/about/components/DevView";
import {LoginServiceProvider} from "../service/registration/login/LoginServiceProvider";
import {SignInServiceProvider} from "../service/registration/signIn/SignInServiceProvider";
import ChannelView from "../view/channel/ChannelView";
import {ChannelServiceProvider} from "../service/channel/ChannelServiceProvider";
import {AuthValidator} from "../view/session/authValidator";
import {FindChannelsView} from "../view/findChannels/FindChannelsView";
import {FindChannelsMockServiceProvider} from "../service/findChannels/mock/FindChannelsMockServiceProvider";

const router = createBrowserRouter(
    [
        {
            "path": "/",
            "element": <Navigate to="/login" replace />,
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
            "path": "/home",
            "element":
                <AuthValidator>
                    <h1>Home</h1>
                </AuthValidator>,
            children: [
                {
                    "path": "channel/:id",
                    "element":
                        <ChannelServiceProvider>
                            <ChannelView/>
                        </ChannelServiceProvider>
                }
            ]
        },
        {
            "path": "/about",
            "element": <DevView />,
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
