import {SignInValidationResponse} from "./SignInValidationResponse";
import {Context, createContext} from "react";
import {Either} from "../../../model/Either";

/**
 * The context for the sign in service.
 *
 * @method signIn
 * @method stateValidator
 */
export interface SignInServiceContext {
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

    /**
     * The state validator method.
     *
     * @param username
     * @param password
     * @param invitationCode
     */
    stateValidator(
        username: string,
        password: string,
        invitationCode: string
    ): Promise<SignInValidationResponse>
}

const defaultSignInServiceContext: SignInServiceContext = {
    signIn: () => {
        return new Promise<Either<AuthInfo, string>>((_, reject) => {
            reject(new Error("Not implemented"))
        })
    },
    stateValidator: () => {
        return new Promise<SignInValidationResponse>((_, reject) => {
            reject(new Error("Not implemented"))
        })
    }
}

export const SignInServiceContext: Context<SignInServiceContext> =
    createContext(defaultSignInServiceContext)