import {Either} from "../../model/Either";
import {createContext} from "react";


export interface CreateChannelInvitationServiceContext {
    createChannelInvitation(
        expirationDate: string,
        maxUses: number,
        accessControl: "READ_ONLY" | "READ_WRITE",
        channelId: string
    ): Promise<Either<{invitationCode: string}, string>>
}

const defaultCreateChannelInvitationServiceContext: CreateChannelInvitationServiceContext = {
    createChannelInvitation: (expirationDate, maxUses, accessControl, channelId) => {
        return new Promise<Either<{invitationCode: string}, string>>((resolve, reject) => {
            reject(new Error("Not implemented"))
        })
    }
}

export const CreateChannelInvitationServiceContext =
    createContext<CreateChannelInvitationServiceContext>(defaultCreateChannelInvitationServiceContext)