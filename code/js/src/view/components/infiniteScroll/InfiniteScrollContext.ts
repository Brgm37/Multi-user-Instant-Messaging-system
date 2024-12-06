import {createContext, ReactNode} from "react";
import useScroll, {UseScrollState} from "../../../service/utils/hooks/useScroll/UseScroll";

/**
 * The context for the InfiniteScroll component.
 * This context is used to provide the InfiniteScroll component with the necessary data to render the list of items.
 *
 * @interface InfiniteScrollContext
 *
 * @template T The type of the items in the list. The items must implement the Identifiable interface.
 *
 * @property list The list of items to render.
 *
 * @property hasMore An object with two boolean properties, `head` and `tail`,
 * that indicate if there are more items to load in the respective direction.
 *
 * @method loadMore A function that is called when the InfiniteScroll component needs to load more items.
 *
 * @property isLoading A boolean that indicates if the InfiniteScroll component is currently loading more items.
 *
 * @property listMaxSize The maximum size of the list of items.
 *
 * @method renderItems A function that is called to render each item in the list.
 *
 * @see Identifiable
 */
export interface InfiniteScrollContext<T extends Identifiable> {
    items: UseScrollState<T>,
    loadMore(offset: number, at: "head" | "tail"): void,
    isLoading: 'head' | 'tail' | false,
    renderItems(item: T): ReactNode;
}

const defaultInfiniteScrollContext: InfiniteScrollContext<Identifiable> = {
    items: {list: [], hasMore: {head: false, tail: false}, max: 0},
    loadMore: () => {throw Error("Not implemented!")},
    isLoading: false,
    renderItems: () => {throw Error("Not implemented!")}
}

export const InfiniteScrollContext =
    createContext<InfiniteScrollContext<Identifiable>>(defaultInfiniteScrollContext)