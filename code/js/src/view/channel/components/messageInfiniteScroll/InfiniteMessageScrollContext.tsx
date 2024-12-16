import {createContext} from "react";

/**
 * The context for the infinite message scroll.
 */
export interface InfiniteMessageScrollContext {
    popUpClick(): void;
}

/**
 * The default infinite message scroll context.
 */
export const InfiniteMessageScrollContext = createContext<InfiniteMessageScrollContext>({
    popUpClick: () => {
        throw new Error('InfiniteMessageScrollContext not implemented');
    },
});


