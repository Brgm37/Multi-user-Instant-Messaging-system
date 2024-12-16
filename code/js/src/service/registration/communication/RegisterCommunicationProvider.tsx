import * as React from "react";
import {useState} from "react";
import {RegisterCommunicationContext, RegisterCommunicationProvider} from "./RegisterCommunicationContext";

/**
 * The provider for the register communication service.
 */
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