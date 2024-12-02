import * as React from "react"
import configJson from "../../../envConfig.json"
import {Navigate, useLocation} from "react-router-dom";
import {getCookie} from "../../service/session/GetCookie";
import {AuthUserContext} from "./AuthUserContext";

/**
 * The authentication token.
 */
const TOKEN = configJson.session

export function AuthValidator({children}: {children: React.ReactNode}): React.ReactElement {
    const cookie = getCookie(TOKEN)
    const location = useLocation()
    if (!cookie) return <Navigate to={"/login"} state={{source: location.pathname}}></Navigate>
    return (
        <AuthUserContext.Provider value={{id: cookie}}>
            {children}
        </AuthUserContext.Provider>
    )
}