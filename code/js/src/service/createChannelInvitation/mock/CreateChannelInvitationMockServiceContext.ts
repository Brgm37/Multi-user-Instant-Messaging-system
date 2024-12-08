import {Either} from "../../../model/Either";
import {createContext} from "react";

/**
 * @description The context for the create channel invitation mock service.
 */
export interface CreateChannelInvitationMockServiceContext {
    createChannelInvitation(
        expirationDate: string,
        maxUses: number,
        accessControl: "READ_ONLY" | "READ_WRITE"
    ): Promise<Either<{invitationCode: string}, string>>
}

/**
 * @description The default create channel invitation mock service context.
 */
const defaultCreateChannelInvitationMockServiceContext: CreateChannelInvitationMockServiceContext = {
    createChannelInvitation: (expirationDate, maxUses, accessControl) => {
        return new Promise<Either<{invitationCode: string}, string>>((resolve, reject) => {
            reject(new Error("Not implemented"))
        })
    }
}

/**
 * @description The context for the create channel invitation mock service.
 */
export const CreateChannelInvitationMockServiceContext =
    createContext<CreateChannelInvitationMockServiceContext>(defaultCreateChannelInvitationMockServiceContext)