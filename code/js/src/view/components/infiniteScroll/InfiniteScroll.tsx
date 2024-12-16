import React, {useCallback, useContext, useEffect, useRef, useState} from "react";
import {InfiniteScrollContext} from "./InfiniteScrollContext";
import {useHeadTail} from "./hooks/useHeadTail";
import LoadingIcon from "../LoadingIcon";

/**
 * InfiniteScroll component
 *
 * The InfiniteScroll component is a React component that provides infinite scrolling functionality.
 *
 * @internal This function is not meant to be used inside an InfiniteMessageScrollContext context.
 *
 * @see InfiniteScrollContext
 *
 * @param className
 * @param scrollStyle
 * @param isToAutoScroll
 */
export default function (
    {className, scrollStyle}: { className?: string, scrollStyle?: string }
): React.JSX.Element {
    const {
        items,
        isLoading,
        loadMore,
        renderItems
    } = useContext(InfiniteScrollContext)

    const [{head, tail}, {setOffset}] = useHeadTail()
    const [at, setAt] = useState<"head" | "tail">("tail")
    const lastObserver = useRef<IntersectionObserver>()
    const firstObserver = useRef<IntersectionObserver>()
    const recordedElementRef = useRef<HTMLElement>()

    const lastItemRef = useCallback((node: HTMLElement) => {
        if (isLoading) return;
        if (lastObserver.current) lastObserver.current.disconnect();
        lastObserver.current = new IntersectionObserver(entries => {
            if (entries[0].isIntersecting && items.hasMore.tail) {
                if (items.list.length == items.max) {
                    setOffset("head", 1);
                }
                setOffset("tail", 1);
                setAt("tail");
                recordedElementRef.current = node;
            }
        })
        if (node) lastObserver.current.observe(node);
    }, [isLoading, items.hasMore.tail, items.list.length, items.max])

    const firstItemRef = useCallback((node: HTMLElement) => {
        if (isLoading) return;
        if (firstObserver.current) firstObserver.current.disconnect();
        firstObserver.current = new IntersectionObserver(entries => {
            if (entries[0].isIntersecting && items.hasMore.head) {
                if (items.list.length == items.max) {
                    setOffset("tail", -1);
                }
                setOffset("head", -1);
                setAt("head");
                recordedElementRef.current = node;
            }
        })
        if (node) firstObserver.current.observe(node);
    }, [isLoading, items.hasMore.head, items.list.length, items.max])

    useEffect(() => {
        if (isLoading || (!items.hasMore.head && !items.hasMore.tail)) return;
        if (at === "head" && items.hasMore.head) loadMore(head, at);
        if (at === "tail" && items.hasMore.tail) loadMore(tail, at);
    }, [head, tail]);

    useEffect(() => {
        if (recordedElementRef.current) {
            if (at === "head") recordedElementRef.current.scrollIntoView({behavior: "smooth", block: "end"})
            else recordedElementRef.current.scrollIntoView({behavior: "smooth", block: "start"})
        }
    }, [items.list]);

    return (
        <div className={className}>
            <ul className={scrollStyle}>
                {isLoading === 'head' && (<LoadingIcon/>)}
                {
                    items.list.map((item, index) => {
                        if (index === 0) {
                            return (
                                <li key={item.id} ref={firstItemRef}>
                                    {renderItems(item)}
                                </li>
                            )
                        } else if (items.list.length === index + 1) {
                            return (
                                <li key={item.id} ref={lastItemRef}>
                                    {renderItems(item)}
                                </li>
                            )
                        } else {
                            return (
                                <li key={item.id}>
                                    {renderItems(item)}
                                </li>
                            )
                        }
                    })
                }
                {isLoading === 'tail' && (<LoadingIcon/>)}
            </ul>
        </div>
    )
}