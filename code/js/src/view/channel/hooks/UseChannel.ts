import {ChannelState} from "./states/ChannelState";
import {ChannelAction} from "./states/ChannelAction";
import {useContext, useEffect, useReducer} from "react";
import {useParams} from "react-router-dom";
import {ChannelServiceContext} from "../../../service/channel/ChannelServiceContext";
import useScroll, {UseScrollHandler, UseScrollState} from "../../../service/utils/hooks/useScroll/UseScroll";
import {compareTimestamps, Message} from "../../../model/Message";
import envConfig from "../../../../envConfig.json"

const LIST_SIZE = envConfig.messages_limit
const DEFAULT_LIMIT = envConfig.default_limit
let limit = DEFAULT_LIMIT
const HAS_MORE = DEFAULT_LIMIT + 1


/**
 * The reducer function for the channel component.
 *
 * @param state
 * @param action
 *
 * @returns ChannelState
 */
function reduce(state: ChannelState, action: ChannelAction): ChannelState {
    switch (state.tag) {
        case "idle":
            switch (action.tag) {
                case "init":
                    return {tag: "loading", messages: state.messages, at: "tail"}
                default:
                    throw new Error(`Invalid action ${action.tag} for state ${state.tag}`)
            }
        case "loading":
            switch (action.tag) {
                case "loadSuccess":
                    return {tag: "messages", messages: action.messages}
                case "loadError":
                    return action.previous
                case "sendSuccess":
                    return {tag: "messages", messages: state.messages}
                case "sendError":
                    return {tag: "error", message: action.error, previous: state}
                default:
                    throw new Error(`Invalid action ${action.tag} for state ${state.tag}`)
            }
        case "error":
            switch (action.tag) {
                case "go-back":
                    return state.previous
                default:
                    throw new Error(`Invalid action ${action.tag} for state ${state.tag}`)
            }
        case "messages":
            switch (action.tag) {
                case "loadMore":
                    return {tag: "loading", messages: state.messages, at: action.at}
                case "sendMessage":
                    return {tag: "loading", messages: state.messages, at: "sending"}
                default:
                    throw new Error(`Invalid action ${action.tag} for state ${state.tag}`)
            }
    }
}

export type UseChannelHandler = {
    initChannel(): void
    loadMore(at: "head" | "tail"): void
    sendMsg(msg: string): void
}

function initList(handler: UseScrollHandler<Message>, messages: Message[]): void {
    const list = messages.slice(0, limit)
    const hasMore = {head: false, tail: messages.length === HAS_MORE}
    handler.reset(list, hasMore)
}

function addItems(
    at: "head" | "tail",
    listHandler: UseScrollHandler<Message>,
    list: UseScrollState<Message>,
    result: Message[]
): void {
    const messages = result.slice(0, limit)
    if (messages.length < limit) limit = messages.length
    else limit = DEFAULT_LIMIT
    const tail =
        result.length === HAS_MORE && at === "tail" ||
        at === "head" && list.list.length === LIST_SIZE
    const hasMore = {
        head: compareTimestamps(messages[messages.length -1], list.list[0]) < 0 && list.list.length === LIST_SIZE,
        tail
    }
    listHandler.addItems(messages, at, hasMore)
}

/**
 * The hook to use the channel reducer.
 */
export function useChannel(): [ChannelState, UseChannelHandler] {
    const {id} = useParams()
    const [list, listHandler] = useScroll<Message>(LIST_SIZE)
    const initialState: ChannelState = {tag: "idle", messages: list}
    const service = useContext(ChannelServiceContext)
    const [state, dispatcher] = useReducer(reduce, initialState)

    useEffect(() => {
        if (state.tag === "error") return
        if (state.tag === "loading") {
            if (state.messages !== list) {
                dispatcher({tag: "loadSuccess", messages: list})
            }
            console.log("list size", list.list.length)
            console.log("list max", list.max)
            console.log("list ", JSON.stringify(list.list))
        }
    }, [list]);

    const handler: UseChannelHandler = {
        initChannel(): void {
            service
                .loadMore(id, "0", HAS_MORE, "before")
                .then(response => {
                    if (response.tag === "success") initList(listHandler, response.value)
                    else dispatcher({tag: "loadError", error: response.value, previous: state})
                })
            dispatcher({tag: "init"})
        },
        loadMore(at): void {
            const timestamp = at === "head" ? list.list[0].timestamp : list.list[list.list.length - 1].timestamp
            const beforeOrAfter = at === "head" ? "before" : "after"
            console.log("loading more", beforeOrAfter, timestamp)
            service
                .loadMore(id, timestamp, limit, beforeOrAfter)
                .then(response => {
                    if (response.tag === "success") addItems(at, listHandler, list, response.value)
                    else dispatcher({tag: "loadError", error: response.value, previous: state})
                })
            dispatcher({tag:"loadMore", at})
        },
        sendMsg(msg: string): void {
            service
                .sendMsg(id, msg)
                .then(response => {
                    if (response.tag === "success") {
                        addItems("tail", listHandler, list, [response.value])
                        dispatcher({tag: "sendSuccess"})
                    } else dispatcher({tag: "sendError", error: response.value, previous: state})
                })
            dispatcher({tag: "sendMessage"})
        }
    }

    return [state, handler]
}