import * as React from 'react';
import {useFindChannels} from "./hooks/UseFindChannels";
import {Navigate} from "react-router-dom";
import {FindChannelState} from "./hooks/states/FindChannelsState";
import {FindChannelsErrorView} from "./components/FindChannelsErrorView";
import {FindChannelsNavigatingView} from "./components/FindChannelsNavigatingView";
import {FindChannelsLoadingView} from "./components/FindChannelsLoadingView";
import {PublicChannel} from "../../model/PublicChannel";
import {InfiniteScrollContext} from "../components/infiniteScroll/InfiniteScrollContext";
import InputBar from "./components/SearchBar/SearchBarFindChannels";

/**
 * The find channels view.
 */
export function FindChannelsView(): React.JSX.Element {
    const [state, list, searchValue,handler] = useFindChannels();

    if (state.tag === "redirect") {
        const channelId = state.channelId
        return <Navigate to={"/channels/" + channelId} replace={true}/>
    }


    if (state.tag === "idle") {
        handler.onInit()
        return <FindChannelsLoadingView/>
    }

    const provider: InfiniteScrollContext<PublicChannel> = {
        isLoading: state.tag === "loading" ? state.at : false,
        items: list,
        renderItems(item: PublicChannel): React.ReactNode {
            return (<div className="bg-gray-900 rounded-lg overflow-hidden card">
                <img alt={`Server ${item.name}`} className="w-full h-40 object-cover" src={item.icon}/>
                <div className="p-4">
                    <div className="flex items-center space-x-2 mb-2">
                        <img src={item.icon} className="h-10 w-10 rounded-full object-cover object-center"
                             alt={item.name}/>
                        <h3 className="text-lg font-bold text-white">{item.name}</h3>
                    </div>
                    <p className="text-gray-400 mb-4">{item.description}</p>
                    <div className="flex justify-between text-sm text-gray-400">
                        <span className={"font-bold text-white"}>{item.owner}</span>
                    </div>
                    <button className="bg-blue-600 text-white py-2 px-4 rounded hover:bg-blue-700 w-full mt-4"
                            onClick={() => handler.onJoin(item.id)}>Join
                    </button>
                </div>
            </div>)
        },
        loadMore(offset: number, at: "head" | "tail"): void {
            handler.onFetchMore(offset, at)
        }
    }

    const view = ((state: FindChannelState) => {
        switch (state.tag) {
            case "error":
                return <FindChannelsErrorView error={state.error} onClose={handler.onErrorClose}/>
            case "joining":
                return <FindChannelsLoadingView/>
            default:
                return (
                    <InfiniteScrollContext.Provider value={provider}>
                        <FindChannelsNavigatingView/>
                    </InfiniteScrollContext.Provider>
                )
        }
    })
    return (
        <div>
            <header className="bg-gray-900 p-4 flex items-center w-full">
                <h1 className="text-xl font-bold">Find Channels</h1>
                <InputBar handleChange={handler.onSearchChange}/>
            </header>
            <div className={"h-myscreen overflow-y-auto scrollbar-hidden"}>
                <main className={"p-8"}>
                    {state.tag === "scrolling" && searchValue === "" && (
                        <section className="text-center mb-8">
                            <h1 className="text-4xl font-bold">FIND A CHANNEL</h1>
                            <p className="text-gray-400">From gaming, to music, to learning, there's a place for
                                you.</p>
                        </section>
                    )}
                    {view(state)}
                </main>
            </div>
        </div>
    )
}






