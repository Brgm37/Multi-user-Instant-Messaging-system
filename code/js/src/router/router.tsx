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
import {LoginView} from "../view/registration/LoginView";
import {SingInView} from "../view/registration/SingInView";

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
            "element": <SingInView validator={singInValidator}/>,
        }
    ]
)

export function app() {
    createRoot(document.getElementById('container')).render(
        <RouterProvider router={router}/>
    )
}
