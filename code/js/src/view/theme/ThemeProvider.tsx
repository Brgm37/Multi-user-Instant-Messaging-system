import * as React from "react"
import {Context, createContext, useState} from "react"

/**
 * The available themes for the app.
 *
 * @type Theme
 * @value "light" The light theme.
 * @value "dark" The dark theme.
 */
type Theme = "light" | "dark"

/**
 * ThemeContext is a React Context that provides the current theme and a function to change it.
 *
 * @prop theme The current theme.
 * @prop setTheme A function to change the current theme.
 *
 */
interface ThemeContextType {
    theme: Theme
    setTheme: (theme: Theme) => void
}

/**
 * The default theme for the app.
 */
const defaultTheme : ThemeContextType = {
    theme: "light",
    setTheme: () => { throw Error("setTheme function must be overridden") },
}

/**
 * ThemeContext is a React Context that provides the current theme and a function to change it.
 */
export const ThemeContext : Context<ThemeContextType> = createContext<ThemeContextType>(defaultTheme)

/**
 * The ThemeProvider is a React component that provides the current theme and a function to change it.
 *
 * @param children
 * @returns The ThemeProvider component.
 */
export function ThemeProvider({children} : {children: React.ReactNode}) {
    const [theme, setTheme] = useState<Theme>("light")

    return (
        <ThemeContext.Provider value={{theme, setTheme}}>
            {children}
        </ThemeContext.Provider>
    )
}