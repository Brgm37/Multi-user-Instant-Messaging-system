import * as React from 'react'
import {Dev} from "../hooks/states/AboutState";


export function DevView({ dev }: { dev: Dev }): React.JSX.Element  {
    const [showDevInfo, setShowDevInfo] = React.useState(false)
    const handleToggleDevInfo = () => setShowDevInfo(!showDevInfo)
    return (
        <div>
            <button onClick={handleToggleDevInfo}>{dev.name}</button>
            {showDevInfo ? (
                <>
                    <p>{dev.num}</p>
                    <p>{dev.bio || null}</p>
                    <p>{dev.email}</p>
                    <a href={dev.github.href}>Github</a>
                    <p></p>
                    <a href={dev.linkdIn.href}>LinkedIn</a>
                </>
            ) : null}
            <p></p>
        </div>
    )
}