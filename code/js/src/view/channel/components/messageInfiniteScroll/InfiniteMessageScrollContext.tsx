import {createContext} from "react";

/**
 * The context for the infinite message scroll.
 */
export interface InfiniteMessageScrollContext {
    onNewMessage(): void;
}

/**
 * The default infinite message scroll context.
 */
export const InfiniteMessageScrollContext = createContext<InfiniteMessageScrollContext>({
    onNewMessage: () => {
        throw new Error('InfiniteMessageScrollContext not implemented');
    },
});


