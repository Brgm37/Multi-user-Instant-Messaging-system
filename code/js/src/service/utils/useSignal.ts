import {useEffect} from "react";

/**
 * A hook that returns an AbortSignal that is aborted when the component is unmounted.
 */
export default function ():AbortSignal {
    const controller = new AbortController()
    useEffect(() => {
        return () => {
            controller.abort()
        }
    }, []);
    return controller.signal
}