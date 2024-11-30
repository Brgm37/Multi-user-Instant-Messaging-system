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
import {LoginServiceProvider} from "../service/registration/login/LoginServiceProvider";
import {SignInServiceProvider} from "../service/registration/signIn/SignInServiceProvider";
import {FindChannelsView} from "../view/findChannels/FindChannelsView";
import {makeDefaultFindChannelService} from "../service/findChannels/FindChannelService";
import {makeDefaultFindChannelsMockService} from "../service/findChannels/FindChannelsMockService";

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
            "element": <h1>Home</h1>,
        },
        {
            "path": "/findChannels",
            "element":
                <FindChannelsView/>
        }
    ]
)

export function app() {
    createRoot(document.getElementById('container')).render(
        <RouterProvider router={router}/>
    )
}
