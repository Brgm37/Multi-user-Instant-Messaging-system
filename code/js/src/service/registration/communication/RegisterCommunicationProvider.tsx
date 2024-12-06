import * as React from "react";
import {createContext, useState} from "react";

export interface RegisterCommunicationProvider {
    username: string
    password: string
    setUsername (username: string): void
    setPassword (password: string): void
}

const defaultRegisterCommunicationProvider: RegisterCommunicationProvider = {
    username: "",
    password: "",
    setUsername()  {throw new Error("setUsername() not implemented")},
    setPassword()  {throw new Error("setPassword() not implemented")}
}

export const RegisterCommunicationContext =
    createContext<RegisterCommunicationProvider>(defaultRegisterCommunicationProvider)

export function RegisterCommunicationServiceProvider({children}: {children: React.ReactNode}) {
    const [username, setUsername] = useState<string>("")
    const [password, setPassword] = useState<string>("")

    const provider: RegisterCommunicationProvider = {
        username,
        password,
        setUsername,
        setPassword
    }

    return (
        <RegisterCommunicationContext.Provider value={provider}>
            {children}
        </RegisterCommunicationContext.Provider>
    )
}