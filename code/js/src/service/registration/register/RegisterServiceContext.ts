import {Context, createContext} from "react";
import {Either} from "../../../model/Either";

/**
 * The context for the register service.
 *
 * @method signIn
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

/**
 * The default register service context.
 */
const defaultRegisterServiceContext: RegisterServiceContext = {
    signIn: () => {
        return new Promise<Either<AuthInfo, string>>((_, reject) => {
            reject(new Error("Not implemented"))
        })
    }
}

/**
 * The context for the register service.
 */
export const RegisterServiceContext: Context<RegisterServiceContext> =
    createContext(defaultRegisterServiceContext)