import {ChannelState} from "./states/ChannelState";
import {ChannelAction} from "./states/ChannelAction";
import {useContext, useEffect, useReducer} from "react";
import {useParams} from "react-router-dom";
import {ChannelServiceContext} from "../../../service/channel/ChannelServiceContext";
import useScroll from "../../../service/utils/hooks/useScroll/UseScroll";
import {Message} from "../../../model/Message";
import envConfig from "../../../../envConfig.json"
import {SseCommunicationServiceContext} from "../../../service/sse/SseCommunicationService";
import {UseChannelHandler} from "./handler/UseChannelHandler";
import {addItems, addMessage, initList} from "./handler/helper/MessagesLoader";

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
                case "reset":
                    return {tag: "idle", messages: action.messages}
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
                case "receiving-sse":
                    return state
                case "sendError":
                    return {tag: "error", message: action.error, previous: state}
                case "reset":
                    return {tag: "idle", messages: action.messages}
                default:
                    throw new Error(`Invalid action ${action.tag} for state ${state.tag}`)
            }
        case "error":
            switch (action.tag) {
                case "go-back":
                    return state.previous
                case "reset":
                    return {tag: "idle", messages: action.messages}
                default:
                    throw new Error(`Invalid action ${action.tag} for state ${state.tag}`)
            }
        case "messages":
            switch (action.tag) {
                case "loadMore":
                    return {tag: "loading", messages: state.messages, at: action.at}
                case "sendMessage":
                    return {tag: "loading", messages: state.messages, at: "sending"}
                case "receiving-sse":
                    return {tag: "loading", messages: state.messages, at: "head"}
                case "reset":
                    return {tag: "idle", messages: action.messages}
                default:
                    throw new Error(`Invalid action ${action.tag} for state ${state.tag}`)
            }
    }
}

/**
 * The hook to use the channel reducer.
 */
export function useChannel(): [ChannelState, UseChannelHandler] {
    const {id} = useParams()
    const {messages, consumeMessage} = useContext(SseCommunicationServiceContext)
    const [list, listHandler] = useScroll<Message>(LIST_SIZE)
    const initialState: ChannelState = {tag: "idle", messages: list}
    const service = useContext(ChannelServiceContext)
    const [state, dispatcher] = useReducer(reduce, initialState)

    useEffect(() => {
        if (state.tag !== "messages") return
        const toRemove: Message[] = []
        for (const message of messages) {
            if (message.channel == id) {
                if (!list.list.some(m => m.id === message.id)) {
                    dispatcher({tag: "receiving-sse"})
                    addMessage(listHandler, list, message)
                }
                toRemove.push(message)
            }
        }
        if (toRemove.length > 0) consumeMessage(toRemove)
    }, [messages, state]);

    useEffect(() => {
        dispatcher({tag: "reset", messages: list})
    }, [id]);

    useEffect(() => {
        if (state.tag === "error") return
        if (state.tag === "loading") {
            if (state.messages !== list) {
                dispatcher({tag: "loadSuccess", messages: list})
            }
        }
    }, [list]);

    const handler: UseChannelHandler = {
        initChannel(): void {
            service
                .loadMore(id, "0", HAS_MORE, "before")
                .then(response => {
                    if (response.tag === "success") initList(listHandler, response.value, limit, HAS_MORE)
                    else dispatcher({tag: "loadError", error: response.value, previous: state})
                })
            dispatcher({tag: "init"})
        },
        loadMore(at): void {
            const timestamp = at === "head" ?
                list.list[0].timestamp :
                list.list[list.list.length - 1].timestamp
            const beforeOrAfter = at === "head" ? "after" : "before"
            service
                .loadMore(id, timestamp, HAS_MORE, beforeOrAfter)
                .then(response => {
                    if (response.tag === "success")
                        addItems(at, listHandler, list, response.value, limit, DEFAULT_LIMIT, HAS_MORE, LIST_SIZE)
                    else dispatcher({tag: "loadError", error: response.value, previous: state})
                })
            dispatcher({tag: "loadMore", at})
        },
        sendMsg(msg: string): void {
            service
                .sendMsg(id, msg)
                .then(response => {
                    if (response.tag === "success") addMessage(listHandler, list, response.value)
                    else dispatcher({tag: "sendError", error: response.value, previous: state})
                })
            dispatcher({tag: "sendMessage"})
        }
    }

    return [state, handler]
}