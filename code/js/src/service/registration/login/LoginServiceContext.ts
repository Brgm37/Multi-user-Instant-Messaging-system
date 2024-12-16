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

/**
 * The default login service context.
 */
const defaultLoginServiceContext: LoginServiceContext = {
    login: () => {
        throw new Error("Not implemented")
    }
}

/**
 * The context for the login service.
 */
export const LoginServiceContext:Context<LoginServiceContext> =
    createContext<LoginServiceContext>(defaultLoginServiceContext)