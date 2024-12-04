import * as React from "react";
import {Outlet} from "react-router-dom";
import {Sidebar} from "./sideBar/SideBar";

export default function BasicChannelsView(
    {error, errorDismiss}: { error?: string, errorDismiss?: () => void }
): React.JSX.Element {
    return (
        <div className="flex h-screen">
            <Sidebar />
            <div className="flex-1 bg-gray-800 p-4">
                {error && (
                    <div className="bg-red-500 text-white p-2 rounded">
                        {error}
                        <button
                            className="ml-2"
                            onClick={errorDismiss}
                        >X</button>
                    </div>
                )}
                <h1 className="text-center text-4xl font-bold text-gray-200">
                    Welcome to ChIMP
                </h1>
                <Outlet />
            </div>
        </div>
    );
}