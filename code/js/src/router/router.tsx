import * as React from "react"
import {createRoot} from "react-dom/client"
import {loginValidator} from "../service/registration/validation/LoginValidator";
import {singInValidator} from "../service/registration/validation/SingInValidator";

import {
    createBrowserRouter,
    Link, Navigate,
    Outlet,
    RouterProvider,
    useParams,
} from "react-router-dom"
import {LoginView} from "../view/registration/login/LoginView";
import {SignInView} from "../view/registration/signIn/SignInView";

const router = createBrowserRouter(
    [
        {
            "path": "/",
            "element": <Navigate to="/login" replace />,
        },
        {
            "path": "/login",
            "element": <LoginView validator={loginValidator}/>,
        },
        {
            "path": "/signIn",
            "element": <SignInView validator={singInValidator}/>,
        },
        {
            "path": "/home",
            "element": <h1>Home</h1>,
        }
    ]
)

export function app() {
    createRoot(document.getElementById('container')).render(
        <RouterProvider router={router}/>
    )
}
