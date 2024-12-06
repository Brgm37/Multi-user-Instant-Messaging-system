import * as React from 'react';
import {createContext} from "react";

export interface InfiniteMessageScrollContext {
    onNewMessage(): void;
}

export const InfiniteMessageScrollContext = createContext<InfiniteMessageScrollContext>({
    onNewMessage: () => {
        throw new Error('InfiniteMessageScrollContext not implemented');
    },
});


