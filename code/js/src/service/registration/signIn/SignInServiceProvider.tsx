import * as React from "react";
import {SignInServiceContext} from "./SignInServiceContext";
import useSignal from "../../utils/useSignal";
import {urlBuilder} from "../../utils/UrlBuilder";
import {signInValidator} from "../validation/SignInValidator";
import {Either, success, failure} from "../../../model/Either";

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

export function SignInServiceProvider({ children }: { children: React.ReactNode }): React.JSX.Element {
    const signal = useSignal()
    const service : SignInServiceContext = {
        async signIn(username: string, password: string, invitationCode: string): Promise<Either<true, string>> {
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
            if (response.ok) {
                return success(true) as Either<true, string>
            } else {
                return failure(response.text()) as Either<true, string>
            }
        },

        stateValidator: signInValidator
    }
    return (
        <SignInServiceContext.Provider value={service}>
            {children}
        </SignInServiceContext.Provider>
    )
}