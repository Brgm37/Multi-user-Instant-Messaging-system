import {createContext} from "react";

/**
 * The context for the authentication user.
 */
interface AuthUserContext {
    id: string;
}

/**
 * The default context for the authentication user.
 */
export const AuthUserContext = createContext<AuthUserContext | undefined>(undefined);