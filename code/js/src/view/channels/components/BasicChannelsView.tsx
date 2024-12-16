import * as React from "react";
import {Outlet} from "react-router-dom";
import {Sidebar} from "./sideBar/SideBar";

/**
 * The basic view for the channels.
 *
 * @param error
 * @param errorDismiss
 * @constructor
 */
export default function BasicChannelsView(
    {error, errorDismiss}: { error?: string, errorDismiss?: () => void }
): React.JSX.Element {
    return (
        <div className="flex min-h-screen">
            <Sidebar />
            <div className="flex-1 bg-gray-800 flex flex-col min-h-screen">
                {error && (
                    <div className="bg-red-500 text-white p-2 rounded">
                        {error}
                        <button
                            className="ml-2"
                            onClick={errorDismiss}
                        >X</button>
                    </div>
                )}
                <Outlet />
            </div>
        </div>
    );
}