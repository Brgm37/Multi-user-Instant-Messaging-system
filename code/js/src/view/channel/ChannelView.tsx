import * as React from "react";
import {useChannel} from "./hooks/UseChannel";
import {InitLoadingView} from "./components/InitLoadingView";
import {InfiniteScrollContext} from "../components/infiniteScroll/InfiniteScrollContext";
import {Message} from "../../model/Message";
import BasicChannelView from "./components/BasicChannelView";
import {InfiniteMessageScrollContext} from "./components/messageInfiniteScroll/InfiniteMessageScrollContext";
import {AuthUserContext} from "../session/AuthUserContext";
import {useContext} from "react";

/**
 * The default avatar.
 */
const defaultAvatar = "/defaultIcons/blank-default-pfp.webp"

/**
 * The channel view.
 */
export function ChannelView(): React.JSX.Element {
    const [state, messages, handler] = useChannel();
    const { id } = useContext(AuthUserContext);

    if (state.tag === "idle") {
        handler.initChannel();
        return <InitLoadingView />;
    }

    const provider: InfiniteScrollContext<Message> = {
        isLoading: state.tag === "loading" ? state.at : false,
        items: messages,
        renderItems(item: Message): React.ReactNode {
            const isOwner = Number(item.owner.id) === Number(id);
            return (
                <>
                    <div key={item.id} className={`flex items-start mb-4 ${isOwner ? 'justify-end' : ''}`}>
                        {!isOwner && (
                            <img src={defaultAvatar} alt="avatar"
                                 className="h-8 w-8 rounded-full object-cover object-center mr-4"/>
                        )}
                        <div className={`flex flex-col ${isOwner ? 'items-end' : 'items-start'}`}>
                            <div className="text-sm text-gray-400">
                                {!isOwner && (
                                    <span className={`text-sm text-white font-bold`}>
                                        {item.owner.username}
                                     </span>
                                )}
                                <span className="mx-2 text-xxs">
                                    {new Date(item.timestamp).toLocaleString('en-US', {
                                        hour: '2-digit',
                                        minute: '2-digit',
                                        year: 'numeric',
                                        month: '2-digit',
                                        day: '2-digit'
                                    })}
                                </span>
                            </div>
                            <div className="text-sm text-gray-200 break-words whitespace-pre-wrap">
                                {item.text.match(/.{1,50}/g)?.map((line, i) => (
                                    <div key={i}>{line}</div>
                                ))}
                            </div>
                        </div>
                    </div>
                </>
            );
        },
        loadMore(_: number, at: "head" | "tail"): void {
            handler.loadMore(at);
        }
    };

    const onMessage: InfiniteMessageScrollContext = {
        onNewMessage() {
            handler.reset();
        }
    };

    return (
        <InfiniteScrollContext.Provider value={provider}>
            <InfiniteMessageScrollContext.Provider value={onMessage}>
                <BasicChannelView
                    error={state.tag === "error" ? state.message : undefined}
                    errorDismiss={handler.goBack}
                    onSend={handler.sendMsg}
                    onError={handler.error}
                />
            </InfiniteMessageScrollContext.Provider>
        </InfiniteScrollContext.Provider>
    );
}
