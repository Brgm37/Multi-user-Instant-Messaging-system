import {urlBuilder} from "../utils/UrlBuilder";
import useSignal from "../utils/hooks/useSignal/useSignal";
import {Either, failure, success} from "../../model/Either";
import {CreateUserInvitationServiceContext} from "./createUserInvitationServiceContext";
import React from "react";

const baseUrl = urlBuilder("/users")

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
                body: JSON.stringify({expirationDate}),
                signal,
                credentials: "include"
            }
            const response = await fetch(baseUrl + "/invitations", init)
            if (response.ok) {
                const data = await response.json()
                return success(data) as Either<{invitationCode: string}, string>
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