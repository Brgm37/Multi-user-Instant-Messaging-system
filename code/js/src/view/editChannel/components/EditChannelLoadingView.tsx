import * as React from 'react';
import LoadingIcon from "../../components/LoadingIcon";

/**
 * EditChannelLoadingView component
 */
export function EditChannelLoadingView(): React.JSX.Element {
    return (
        <div className="flex items-center justify-center ">
            <LoadingIcon />
        </div>
    )
}