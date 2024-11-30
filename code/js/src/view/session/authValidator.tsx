import * as React from "react"
import configJson from "../../../envConfig.json"
import {Navigate, useLocation} from "react-router-dom";

/**
 * The authentication token.
 */
const TOKEN = configJson.session

function getCookies(): {[key: string]: string}[] {
    return document.cookie.split(";").map((cookie) => {
        const [key, value] = cookie.split("=")
        return {[key]: value}
    })
}

export function AuthValidator({children}: {children: React.ReactNode}): React.ReactElement {
    const cookies = getCookies()
    const location = useLocation()
    if (cookies.some((cookie) => cookie[TOKEN])) {
        return <>{children}</>
    }
    else {
        return <Navigate to={"/login"} state={{source: location.pathname}}></Navigate>
    }
}