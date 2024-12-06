import React, {useCallback, useContext, useEffect, useRef, useState} from "react";
import {InfiniteScrollContext} from "../../../components/infiniteScroll/InfiniteScrollContext";
import {useHeadTail} from "../../../components/infiniteScroll/hooks/useHeadTail";
import LoadingIcon from "../../../components/LoadingIcon";
import {InfiniteMessageScrollContext} from "./InfiniteMessageScrollContext";

const TIMEOUT = 5000

export default function (
    {className, scrollStyle}: { className?: string, scrollStyle?: string }
): React.JSX.Element {
    const {
        items,
        isLoading,
        loadMore,
        renderItems
    } = useContext(InfiniteScrollContext)

    const context = useContext(InfiniteMessageScrollContext)

    const [{head, tail}, {setOffset}] = useHeadTail()
    const [at, setAt] = useState<"head" | "tail" | "sending" | "receiving">("head")
    const [pop, setPop] = useState<boolean>(false)
    const lastObserver = useRef<IntersectionObserver>()
    const firstObserver = useRef<IntersectionObserver>()
    const recordedElementRef = useRef<HTMLElement>()
    const beginOfListRef = useRef<HTMLDivElement>(null)

    const lastItemRef = useCallback((node: HTMLElement) => {
        if (isLoading) return;
        if (lastObserver.current) lastObserver.current.disconnect();
        lastObserver.current = new IntersectionObserver(entries => {
            if (entries[0].isIntersecting && items.hasMore.tail) {
                if (items.list.length == items.max) setOffset("head", 1);
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
                if (items.list.length == items.max) setOffset("tail", -1);
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
        if (isLoading === "sending") setAt("sending")
        let timeout: NodeJS.Timeout
        if (isLoading === 'receiving') {
            setAt("receiving")
            setPop(true)
            timeout = setTimeout(() => setPop(false), TIMEOUT)
        }
        return () => clearTimeout(timeout)
    }, [isLoading]);

    useEffect(() => {
        if (recordedElementRef.current) {
            switch (at) {
                case "head":
                    recordedElementRef.current.scrollIntoView({behavior: "smooth", block: "end"})
                    break;
                case "tail":
                    recordedElementRef.current.scrollIntoView({behavior: "smooth", block: "start"})
                    break;
                default:
                    break;
            }
        }
    }, [items.list]);

    const handlePopupClick = () => {
        if (items.hasMore.head) {
            context.onNewMessage()
        } else {
            if (beginOfListRef.current) {
                beginOfListRef.current.scrollIntoView({behavior: "smooth"});
            }
        }
        setPop(false)
    };

    useEffect(() => {
        if (beginOfListRef.current) beginOfListRef.current.scrollIntoView({behavior: "smooth"});
    });

    return (
        <div className={className}>
            <ul className={scrollStyle}>
                <div>{isLoading === 'head' && (<LoadingIcon/>)}</div>
                <div ref={beginOfListRef}></div>
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
                {pop &&
                    (
                        <div
                            className="popup"
                            onClick={handlePopupClick}
                        >
                            New message received!
                        </div>
                    )
                }
                <div>{isLoading === 'tail' && (<LoadingIcon/>)}</div>
            </ul>
        </div>
    )
}