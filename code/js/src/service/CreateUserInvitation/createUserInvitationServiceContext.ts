import {Either} from "../../model/Either";
import {Context, createContext} from "react";

export interface CreateUserInvitationServiceContext {
    createUserInvitation(
        expirationDate: string
    ): Promise<Either<{invitationCode: string}, string>>
}

const defaultCreateUserInvitationServiceContext: CreateUserInvitationServiceContext = {
    createUserInvitation: (expirationDate) => {
        return new Promise<Either<{invitationCode: string}, string>>((_, reject) => {
            reject(new Error("Not implemented"))
        })
    }
}

export const CreateUserInvitationServiceContext: Context<CreateUserInvitationServiceContext> =
    createContext(defaultCreateUserInvitationServiceContext)