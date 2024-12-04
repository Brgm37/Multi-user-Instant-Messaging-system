import {Context, createContext} from "react";
import {Either} from "../../../model/Either";

/**
 * The context for the register service.
 *
 * @method signIn
 * @method stateValidator
 */
export interface RegisterServiceContext {
    /**
     * The sign in method.
     *
     * @param username
     * @param password
     * @param invitationCode
     */
    signIn(
        username: string,
        password: string,
        invitationCode: string,
    ): Promise<Either<AuthInfo, string>>
}

const defaultRegisterServiceContext: RegisterServiceContext = {
    signIn: () => {
        return new Promise<Either<AuthInfo, string>>((_, reject) => {
            reject(new Error("Not implemented"))
        })
    }
}

export const RegisterServiceContext: Context<RegisterServiceContext> =
    createContext(defaultRegisterServiceContext)