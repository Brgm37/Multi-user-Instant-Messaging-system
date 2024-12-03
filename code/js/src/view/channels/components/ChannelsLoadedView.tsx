import * as React from "react";
import {PartialNameSearchBar} from "./helpers/PartialNameSearchBar";
import InfiniteScroll from "../../components/infiniteScroll/InfiniteScroll";
import {Outlet} from "react-router-dom";
import MenuBar from "./helpers/MenuBar";

export default function (): React.JSX.Element {
    return (
        <div>
            <PartialNameSearchBar/>
            <MenuBar/>
            <InfiniteScroll/>
            <Outlet/>
        </div>
    )
}