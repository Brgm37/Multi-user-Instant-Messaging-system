import * as React from "react";
import {DemoSearchBar} from "./helpers/DemoSearchBar";
import InfiniteScroll from "../../components/infiniteScroll/InfiniteScroll";
import {Outlet} from "react-router-dom";
import MenuBar from "./helpers/MenuBar";

export default function (): React.JSX.Element {
    return (
        <div>
            <DemoSearchBar/>
            <MenuBar/>
            <InfiniteScroll/>
            <Outlet/>
        </div>
    )
}