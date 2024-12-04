import {Context, createContext} from "react";
import {Either} from "../../../model/Either";

/**
 * The login service context.
 *
 * @method login
 */
export interface LoginServiceContext {
    /**
     * The login method.
     *
     * @param username
     * @param password
     */
    login(
        username: string,
        password: string,
    ): Promise<Either<AuthInfo, string>>
}

const defaultLoginServiceContext: LoginServiceContext = {
    login: () => {
        return new Promise<Either<AuthInfo, string>>((resolve, reject) => {
            reject(new Error("Not implemented"))
        })
    }
}

export const LoginServiceContext:Context<LoginServiceContext> =
    createContext<LoginServiceContext>(defaultLoginServiceContext)