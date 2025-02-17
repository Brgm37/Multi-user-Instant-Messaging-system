import {Cookies} from "react-cookie";
import {CookieOptions} from "react-router-dom";

/**
 * Set a cookie.
 *
 * @param name
 * @param value
 * @param seconds
 */
export function setCookie(name: string, value: string, seconds: number) {
    const cookie = new Cookies()
    const expires = new Date()
    expires.setSeconds(expires.getSeconds() + seconds)
    const options: CookieOptions = {
        expires,
        path: "/",
        httpOnly: false,
        secure: false,
    }
    cookie.set(name, value, options)
}