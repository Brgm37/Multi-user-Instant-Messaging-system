import {Either} from "../../../model/Either";
import {createContext} from "react";


export interface CreateChannelInvitationMockServiceContext {
    createChannelInvitation(
        expirationDate: string,
        maxUses: number,
        accessControl: "READ_ONLY" | "READ_WRITE"
    ): Promise<Either<{invitationCode: string}, string>>
}

const defaultCreateChannelInvitationMockServiceContext: CreateChannelInvitationMockServiceContext = {
    createChannelInvitation: (expirationDate, maxUses, accessControl) => {
        return new Promise<Either<{invitationCode: string}, string>>((resolve, reject) => {
            reject(new Error("Not implemented"))
        })
    }
}

export const CreateChannelInvitationMockServiceContext =
    createContext<CreateChannelInvitationMockServiceContext>(defaultCreateChannelInvitationMockServiceContext)