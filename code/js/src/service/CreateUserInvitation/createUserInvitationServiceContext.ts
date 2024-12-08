import {Either} from "../../model/Either";
import {Context, createContext} from "react";

/**
 * The context for the create user invitation service.
 *
 * @method createUserInvitation
 */
export interface CreateUserInvitationServiceContext {
    /**
     * CreateUserInvitation creates a user invitation.
     *
     * @param expirationDate The expiration date for the invitation.
     * @returns The invitation code.
     */
    createUserInvitation(
        expirationDate: string
    ): Promise<Either<{invitationCode: string}, string>>
}

/**
 * The default create user invitation service context.
 */
const defaultCreateUserInvitationServiceContext: CreateUserInvitationServiceContext = {
    createUserInvitation: (expirationDate) => {
        return new Promise<Either<{invitationCode: string}, string>>((_, reject) => {
            reject(new Error("Not implemented"))
        })
    }
}

/**
 * The context for the create user invitation service.
 */
export const CreateUserInvitationServiceContext: Context<CreateUserInvitationServiceContext> =
    createContext(defaultCreateUserInvitationServiceContext)