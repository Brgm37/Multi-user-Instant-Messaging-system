import {Cookies} from "react-cookie";
import {CookieOptions} from "react-router-dom";

export function setCookie(name: string, value: string, days: number) {
    const cookie = new Cookies()
    const expires = new Date()
    expires.setTime(expires.getTime() + days * 24 * 60 * 60 * 1000)
    const options: CookieOptions = {
        expires,
        path: "/",
        httpOnly: false,
        secure: false,
    }
    cookie.set(name, value, {expires})
}