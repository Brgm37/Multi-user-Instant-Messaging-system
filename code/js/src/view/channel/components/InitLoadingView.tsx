import * as React from 'react';
import LoadingIcon from "../../components/LoadingIcon";

/**
 * The initial loading view.
 */
export function InitLoadingView(): React.JSX.Element {
    return (
        <div className={"content-center"}>
            <LoadingIcon/>
        </div>
    )
}