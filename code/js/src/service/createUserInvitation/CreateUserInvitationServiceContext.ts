import {Either} from "../../model/Either";
import {Context, createContext} from "react";

/**
 * The context for the createUser invitation service.
 *
 * @method createUserInvitation
 */
export interface CreateUserInvitationServiceContext {
    /**
     * createUserInvitation creates a user invitation.
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
    createUserInvitation: () => {
        throw new Error("Not implemented")
    }
}

/**
 * The context for the createUser invitation service.
 */
export const CreateUserInvitationServiceContext: Context<CreateUserInvitationServiceContext> =
    createContext(defaultCreateUserInvitationServiceContext)