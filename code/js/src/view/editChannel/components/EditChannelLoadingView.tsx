import * as React from 'react';
import LoadingIcon from "../../components/LoadingIcon";

export function EditChannelLoadingView(): React.JSX.Element {
    return (
        <div className="flex items-center justify-center h-screen">
            <LoadingIcon />
        </div>
    )
}