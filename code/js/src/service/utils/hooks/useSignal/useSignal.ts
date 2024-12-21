import {useEffect} from "react";

/**
 * A hook that returns an AbortSignal that is aborted when the component is unmounted.
 */
export default function ():AbortSignal {
    const controller = new AbortController()
    useEffect(() => {
        return () => {
            controller.abort("Component unmounted")
        }
    }, []);
    return controller.signal
}