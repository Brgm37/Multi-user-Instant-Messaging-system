import {urlBuilder} from "../utils/UrlBuilder";
import useSignal from "../utils/hooks/useSignal/useSignal";
import {Either, failure, success} from "../../model/Either";
import {CreateUserInvitationServiceContext} from "./createUserInvitationServiceContext";
import React from "react";

/**
 * The URL for the user.
 */
const baseUrl = urlBuilder("/users")

/**
 * The create user invitation service provider.
 */
export function CreateUserInvitationServiceProvider(
    {children}: { children: React.ReactNode }
): React.JSX.Element {
    const signal = useSignal()
    const service: CreateUserInvitationServiceContext = {
        async createUserInvitation(
            expirationDate: string
        ): Promise<Either<{invitationCode: string}, string>> {
            const init: RequestInit = {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                signal,
                credentials: "include",
                body: JSON.stringify({expirationDate})
            }
            const response = await fetch(baseUrl + "/invitation", init)
            if (response.ok) {
                const code = await response.json()
                const invitationCode = code.invitationCode
                return success({invitationCode}) as Either<{invitationCode: string}, string>
            } else {
                const error = await response.text()
                return failure(error) as Either<{invitationCode: string}, string>
            }
        }
    }
    return (
        <CreateUserInvitationServiceContext.Provider value={service}>
            {children}
        </CreateUserInvitationServiceContext.Provider>
    )

}