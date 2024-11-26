import * as React from 'react'
import {Link} from "react-router-dom";

export function AboutView(): React.JSX.Element  {
    return (
        <div>
            <h1>About</h1>
            <p>This is the about page.</p>
            <Link to="/">Home</Link>
        </div>
    )
}