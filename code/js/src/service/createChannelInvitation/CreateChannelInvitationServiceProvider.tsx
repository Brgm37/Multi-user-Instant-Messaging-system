import React, {ReactNode} from "react";
import {Either, failure, success} from "../../model/Either";
import useSignal from "../utils/hooks/useSignal/useSignal";
import {AccessControl} from "../../model/AccessControl";
import {urlBuilder} from "../utils/UrlBuilder";
import { CreateChannelInvitationServiceContext } from "./CreateChannelInvitationServiceContext";

const baseUrl = urlBuilder("/channels")


export function CreateChannelInvitationServiceProvider(props: { children: ReactNode }): React.JSX.Element {
    const signal = useSignal()
    const service = {
        async createChannelInvitation(
            expirationDate: string,
            maxUses: number,
            accessControl: AccessControl,
            cId: string
        ): Promise<Either<{invitationCode: string}, string>> {
            const channelId = Number(cId)
            const init: RequestInit = {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                signal,
                credentials: "include",
                body: JSON.stringify({expirationDate, maxUses, accessControl, channelId})
            }
            const url = baseUrl + "/invitations"
            const response = await fetch(url, init);
            if (response.ok) {
                const invCode = await response.json()
               const invitationCode = invCode.invitationCode
                return success({invitationCode}) as Either<{invitationCode: string}, string>
            } else {
                return failure(await response.text()) as Either<{invitationCode: string}, string>
            }
        }
        }
    return (
        <CreateChannelInvitationServiceContext.Provider value={service}>
            {props.children}
        </CreateChannelInvitationServiceContext.Provider>
    )
}