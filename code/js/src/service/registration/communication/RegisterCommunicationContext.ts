import * as React from "react";
import {createContext} from "react";

/**
 * The context for the register communication service.
 *
 * @method setUsername
 * @method setPassword
 */
export interface RegisterCommunicationProvider {
    username: string
    password: string
    setUsername (username: string): void
    setPassword (password: string): void
}

/**
 * The default register communication service context.
 */
const defaultRegisterCommunicationProvider: RegisterCommunicationProvider = {
    username: "",
    password: "",
    setUsername()  {throw new Error("setUsername() not implemented")},
    setPassword()  {throw new Error("setPassword() not implemented")}
}

/**
 * The context for the register communication service.
 */
export const RegisterCommunicationContext =
    createContext<RegisterCommunicationProvider>(defaultRegisterCommunicationProvider)
