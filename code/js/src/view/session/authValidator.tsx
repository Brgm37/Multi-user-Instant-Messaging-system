import * as React from "react"
import configJson from "../../../envConfig.json"
import {Navigate, useLocation} from "react-router-dom";
import {getCookie} from "../../service/session/GetCookie";
import {AuthUserContext} from "./AuthUserContext";
import {urlBuilder} from "../../service/utils/UrlBuilder";
import useSignal from "../../service/utils/hooks/useSignal/useSignal";
import {useEffect, useState} from "react";

const userUrl = urlBuilder("/users")

/**
 * The authentication token.
 */
const TOKEN = configJson.session

/**
 * The authentication validator.
 */
export function AuthValidator({children}: { children: React.ReactNode }): React.ReactElement {
    const signal = useSignal()
    const cookie = getCookie(TOKEN)
    const location = useLocation()
    const [valid, setValid] = useState<boolean>(undefined)
    const init: RequestInit = {
        method: "GET",
        headers: {"Content-Type": "application/json"},
        signal,
        credentials: "include"
    }

    useEffect(() => {
        if (cookie) fetch(`${userUrl}/${cookie}`, init).then(response => setValid(response.ok))
        else setValid(false)
    }, [location.pathname]);

    if (valid === undefined) return <div></div>
    else if (!valid) return <Navigate to={"/register/login"} state={{source: location.pathname}}></Navigate>
    return (
        <AuthUserContext.Provider value={{id: cookie}}>
            {children}
        </AuthUserContext.Provider>
    )
}