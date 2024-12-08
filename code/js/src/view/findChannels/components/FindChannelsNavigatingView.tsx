import React from "react";
import InfiniteScroll from "../../components/infiniteScroll/InfiniteScroll";

export function FindChannelsNavigatingView(): React.JSX.Element {
    return (
        <div>
            <section className={"p-8"}>
                <InfiniteScroll
                    className={""}
                    scrollStyle={"grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4"}
                />
            </section>
        </div>
    )
}