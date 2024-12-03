import {createContext} from "react";

interface AuthUserContext {
    id: string;
}

export const AuthUserContext = createContext<AuthUserContext | undefined>(undefined);