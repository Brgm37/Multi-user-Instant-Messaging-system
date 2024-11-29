import * as React from 'react'
import {LoginServiceContext} from "./LoginServiceContext";
import useSignal from "../../utils/useSignal";
import {urlBuilder} from "../../utils/UrlBuilder";
import {loginValidator} from "../validation/LoginValidator";

/**
 * The URL for the login API.
 */
const loginApiUrl = urlBuilder("/users/login")

/**
 * The header for the username.
 */
const usernameHeader = "username"

/**
 * The header for the password.
 */
const passwordHeader = "password"

export function LoginServiceProvider({ children }: { children: React.ReactNode }): React.JSX.Element {
    const signal = useSignal()
    const service : LoginServiceContext = {
        async login(username: string, password: string): Promise<true | string> {
            const init: RequestInit = {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({[usernameHeader]: username, [passwordHeader]: password}),
                signal,
                credentials: "include"
            }
            const response = await fetch(loginApiUrl, init);
            if (response.ok) {
                return true
            } else {
                return response.text()
            }
        },
        stateValidator: loginValidator
    }
    return (
        <LoginServiceContext.Provider value={service}>
            {children}
        </LoginServiceContext.Provider>
    )
}