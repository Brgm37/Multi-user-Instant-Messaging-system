import { Cookies } from "react-cookie";

export default function (name: string) {
    const cookie = new Cookies();
    cookie.remove(name);
}