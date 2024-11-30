import * as React from "react"
import {createRoot} from "react-dom/client"
import {loginValidator} from "../service/registration/validation/LoginValidator";
import {signInValidator} from "../service/registration/validation/SignInValidator";

import {
    createBrowserRouter,
    Link, Navigate,
    Outlet,
    RouterProvider,
    useParams,
} from "react-router-dom"
import {LoginView} from "../view/registration/login/LoginView";
import {SignInView} from "../view/registration/signIn/SignInView";
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
            "element": <LoginView service={undefined}/>,
        },
        {
            "path": "/signIn",
            "element": <SignInView service={undefined}/>,
        },
        {
            "path": "/home",
            "element": <h1>Home</h1>,
        },
        {
            "path": "/findChannels",
            "element":
                <FindChannelsView service={makeDefaultFindChannelsMockService()}/>
        }
    ]
)

export function app() {
    createRoot(document.getElementById('container')).render(
        <RouterProvider router={router}/>
    )
}
