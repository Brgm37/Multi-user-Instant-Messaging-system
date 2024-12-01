import {LoginValidationResponse} from "./LoginValidationResponse";
import {Context, createContext} from "react";
import {Either} from "../../../model/Either";

/**
 * The login service context.
 *
 * @method login
 * @method stateValidator
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
    ): Promise<Either<true, string>>

    /**
     * The state validator method.
     *
     * @param username
     * @param password
     */
    stateValidator(username: string, password: string): Promise<LoginValidationResponse>
}

const defaultLoginServiceContext: LoginServiceContext = {
    login: (username, password) => {
        return new Promise<Either<true, string>>((resolve, reject) => {
            reject(new Error("Not implemented"))
        })
    },
    stateValidator(username, password) {
        return new Promise<LoginValidationResponse>((resolve, reject) => {
            reject(new Error("Not implemented"))
        })
    }
}

export const LoginServiceContext:Context<LoginServiceContext> =
    createContext<LoginServiceContext>(defaultLoginServiceContext)