import {Either} from "../../model/Either";
import {createContext} from "react";
import {AccessControl} from "../../model/AccessControl";

/**
 * The context for the createChannel invitation service.
 *
 * @method createChannelInvitation
 */
export interface CreateChannelInvitationServiceContext {

    /**
     * ChannelInvitation creates a channel invitation.
     *
     * @param expirationDate The expiration date for the invitation.
     * @param maxUses The maximum number of uses for the invitation.
     * @param accessControl The access control for the invitation.
     * @param channelId The channel ID for the invitation.
     * @returns The invitation code.
     */
    createChannelInvitation(
        expirationDate: string,
        maxUses: number,
        accessControl: AccessControl,
        channelId: string
    ): Promise<Either<{invitationCode: string}, string>>
}

/**
 * The default create channel invitation service context.
 */
const defaultCreateChannelInvitationServiceContext: CreateChannelInvitationServiceContext = {
    createChannelInvitation: () => {
        return new Promise<Either<{invitationCode: string}, string>>((resolve, reject) => {
            reject(new Error("Not implemented"))
        })
    }
}

/**
 * The context for the createChannel invitation service.
 */
export const CreateChannelInvitationServiceContext =
    createContext<CreateChannelInvitationServiceContext>(defaultCreateChannelInvitationServiceContext)