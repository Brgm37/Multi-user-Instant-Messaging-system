// import React, {useCallback, useContext, useEffect, useRef, useState} from "react";
// import {InfiniteScrollContext} from "./InfiniteScrollContext";
// import {useHeadTail} from "./hooks/useHeadTail";
// import "../../../styles/InfiniteScrollMock.css"; //TODO: Check the use of static css file
//
// /**
//  * InfiniteScroll component
//  *
//  * The InfiniteScroll component is a React component that provides infinite scrolling functionality.
//  *
//  * @internal This function is not meant to be used inside an InfiniteScrollContext context.
//  *
//  * @see InfiniteScrollContext
//  *
//  * @param className
//  */
// export default function (
//     {className, scrollStyle}: { className?: string, scrollStyle?: string }
// ): React.JSX.Element {
//     const {
//         list,
//         hasMore,
//         isLoading,
//         listMaxSize,
//         loadMore,
//         renderItems
//     } = useContext(InfiniteScrollContext)
//     const [{head, tail}, {setOffset}] = useHeadTail()
//     const [at, setAt] = useState<"head" | "tail">("tail")
//     const lastObserver = useRef<IntersectionObserver>()
//     const firstObserver = useRef<IntersectionObserver>()
//     const recordedElementRef = useRef<HTMLElement>()
//
//     const lastChannelElementRef = useCallback((node: HTMLElement) => {
//         if (isLoading) return;
//         if (lastObserver.current) lastObserver.current.disconnect();
//         lastObserver.current = new IntersectionObserver(entries => {
//             if (entries[0].isIntersecting && hasMore.tail) {
//                 if (list.length == listMaxSize) {
//                     setOffset("head", 1);
//                 }
//                 setOffset("tail", 1);
//                 setAt("tail");
//                 recordedElementRef.current = node;
//             }
//         });
//         if (node) lastObserver.current.observe(node);
//     }, [isLoading, hasMore.tail, list.length, listMaxSize]);
//
//     const firstChannelElementRef = useCallback((node: HTMLElement) => {
//         if (isLoading) return;
//         if (firstObserver.current) firstObserver.current.disconnect();
//         firstObserver.current = new IntersectionObserver(entries => {
//             if (entries[0].isIntersecting && hasMore.head) {
//                 if (list.length == listMaxSize) {
//                     setOffset("tail", -1);
//                 }
//                 setOffset("head", -1);
//                 setAt("head");
//                 recordedElementRef.current = node;
//             }
//         });
//         if (node) firstObserver.current.observe(node);
//     }, [isLoading, hasMore.head, list.length, listMaxSize]);
//
//     useEffect(() => {
//         if (isLoading || (!hasMore.head && !hasMore.tail)) return;
//         if (at === "head" && hasMore.head) loadMore(head, at);
//         if (at === "tail" && hasMore.tail) loadMore(tail, at);
//     }, [head, tail, at]);
//
//     useEffect(() => {
//         if (recordedElementRef.current) {
//             if (at === "head") recordedElementRef.current.scrollIntoView({behavior: "smooth", block: "end"})
//             else recordedElementRef.current.scrollIntoView({behavior: "smooth", block: "start"})
//         }
//     }, [list]);
//
//     return (
//         <div className={className}>
//             <ul className={scrollStyle}>
//                 <div>{isLoading === 'head' && 'Loading...'}</div>
//                 {
//                     list.map((item, index) => {
//                         if (index === 0) {
//                             return (
//                                 <li key={item.id} ref={firstChannelElementRef}>
//                                     {renderItems(item)}
//                                 </li>
//                             )
//                         } else if (list.length === index + 1) {
//                             return (
//                                 <li key={item.id} ref={lastChannelElementRef}>
//                                     {renderItems(item)}
//                                 </li>
//                             )
//                         } else {
//                             return (
//                                 <li key={item.id}>
//                                     {renderItems(item)}
//                                 </li>
//                             )
//                         }
//                     })
//                 }
//                 <div>{isLoading === 'tail' && 'Loading...'}</div>
//             </ul>
//         </div>
//     )
// }