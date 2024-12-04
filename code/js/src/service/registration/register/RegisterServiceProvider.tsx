import * as React from "react";
import {RegisterServiceContext} from "./RegisterServiceContext";
import useSignal from "../../utils/hooks/useSignal/useSignal";
import {urlBuilder} from "../../utils/UrlBuilder";
import {Either} from "../../../model/Either";
import responseHandler from "../responseHandler/ResponseHandler";

/**
 * The URL for the sign in API.
 */
const signInApiUrl = urlBuilder("/users/signup")

/**
 * The header for the username.
 */
const usernameHeader = "username"

/**
 * The header for the password.
 */
const passwordHeader = "password"

/**
 * The header for the invitation code.
 */
const invitationCodeHeader = "invitationCode"

/**
 * The provider for the register service.
 *
 * @param children
 */
export function RegisterServiceProvider({ children }: { children: React.ReactNode }): React.JSX.Element {
    const signal = useSignal()
    const service : RegisterServiceContext = {
        async signIn(username: string, password: string, invitationCode: string): Promise<Either<AuthInfo, string>> {
            const init: RequestInit = {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({
                    [usernameHeader]: username,
                    [passwordHeader]: password,
                    [invitationCodeHeader]: invitationCode
                }),
                signal,
                credentials: "include"
            }
            const response = await fetch(signInApiUrl, init);
            return responseHandler(response)
        },
    }
    return (
        <RegisterServiceContext.Provider value={service}>
            {children}
        </RegisterServiceContext.Provider>
    )
}