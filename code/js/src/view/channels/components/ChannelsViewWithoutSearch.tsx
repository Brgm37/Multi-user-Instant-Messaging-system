import * as React from "react";
import {ChannelsMenuContext} from "./ChannelsMenuContext";
import {useContext} from "react";
import {PartialNameSearchBar} from "./helpers/PartialNameSearchBar";
import {Outlet} from "react-router-dom";
import MenuBar from "./helpers/MenuBar";

export default function (): React.JSX.Element {
    const context = useContext(ChannelsMenuContext)
    const [page, setPage] = React.useState<number>(0);

    return (
        <div>
            <PartialNameSearchBar className={"bg-gray-700 text-white p-2 rounded"}/>
            <MenuBar/>
            <Outlet/>
        </div>
    )
}