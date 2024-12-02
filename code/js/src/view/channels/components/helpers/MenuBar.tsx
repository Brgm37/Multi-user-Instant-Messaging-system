import * as React from "react";
import {Link} from "react-router-dom";

export default function MenuBar() {
    return (
        <div>
            <Link to={"/"}>Home</Link>
            <Link to={"/searchChannels"}>Search</Link>
        </div>
    )
}