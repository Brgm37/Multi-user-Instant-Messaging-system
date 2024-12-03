import * as React from 'react'
import {Dev} from "../hooks/states/AboutState";
import {useAboutService} from "../hooks/UseAboutForm";


export function DevView(
    {dev}: { dev: Dev}): React.JSX.Element  {
    const [state, handler] = useAboutService()
    const onClick = (dev: Dev) => {
        handler.onSelectDev(dev)
    }

    return (
        <div>
            <button onClick={() => onClick(dev)}>
                <img
                    src={dev.imageURL}
                    style={{width: "30px", height: "30px", borderRadius: "50%"}}
                />
                {dev.name}
            </button>
            {state.tag === "devView" && (
                <>
                    <p>{dev.num}</p>
                    <p>{dev.bio || null}</p>
                    <p>{dev.email}</p>
                    <a href={dev.github.href} onClick={handler.onAcessGithub}>GitHub</a>
                    <br/>
                    <a href={dev.linkdIn.href} onClick={handler.onAcessLinkedIn}>LinkedIn</a>
                </>
            )}
        </div>

    )
}