import { Cookies } from "react-cookie";

/**
 * Remove a cookie.
 *
 * @param name The name of the cookie.
 */
export default function (name: string) {
    const cookie = new Cookies();
    cookie.remove(name, {path: "/"});
}