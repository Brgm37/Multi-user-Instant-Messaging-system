import {ChannelState, makeChannelInitialState} from "./states/ChannelState";
import {ChannelAction} from "./states/ChannelAction";
import {useContext, useReducer} from "react";
import {useParams} from "react-router-dom";
import {ChannelServiceContext} from "../../../service/channel/ChannelServiceContext";

/**
 * The limit of messages to load at once.
 */
const LIMIT = 10

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
            switch (action.type) {
                case "startUp":
                    return {tag: "init"}
                default:
                    throw new Error(`Invalid action: ${action}`)
            }
        case "init":
            switch (action.type) {
                case "loaded-channel":
                    return {tag: "loaded", messages: action.channel.messages, hasMore: action.channel.hasMore}
                case "error":
                    return {tag: "error", error: action.error, previous: action.previous}
                default:
                    throw new Error(`Invalid action: ${action}`)
            }
        case "loaded":
            switch (action.type) {
                case "load-more":
                    return {tag: "loading", messages: state.messages, hasMore: state.hasMore, intent: ["loadMore"]}
                case "send-msg":
                    return {tag: "loading", messages: state.messages, hasMore: state.hasMore, intent: ["sendMessage"]}
                case "error":
                    return {tag: "error", error: action.error, previous: state}
                default:
                    throw new Error(`Invalid action: ${action}`)
            }
        case "loading":
            switch (action.type) {
                case "loaded-more": {
                    const messages =
                        state
                            .messages
                            .concat(action.messages)
                    const current =
                        state
                            .intent
                            .filter((intent) => intent !== "loadMore")
                    if (current.length === 0) return {tag: "loaded", messages: messages, hasMore: action.hasMore}
                    else return {tag: "loading", messages: messages, hasMore: action.hasMore, intent: current}
                }
                case "mag-sent": {
                    const current =
                        state
                            .intent
                            .filter((intent) => intent !== "sendMessage")
                    if (current.length === 0) return {tag: "loaded", messages: state.messages, hasMore: state.hasMore}
                    else return {tag: "loading", messages: state.messages, hasMore: state.hasMore, intent: current}
                }
                case "load-more": {
                    const current = state.intent.concat("loadMore")
                    return {tag: "loading", messages: state.messages, hasMore: state.hasMore, intent: current}
                }
                case "send-msg": {
                    const current = state.intent.concat("sendMessage")
                    return {tag: "loading", messages: state.messages, hasMore: state.hasMore, intent: current}
                }
                default:
                    throw new Error(`Invalid action: ${action}`)
            }
        case "error":
            switch (action.type) {
                case "go-back":
                    return state.previous
            }
    }
}

export type UseChannelHandler = {
    initChannel(): void
    loadMore(): void
    sendMsg(msg: string): void
}

/**
 * The hook to use the channel reducer.
 */
export function useChannel(): [ChannelState, UseChannelHandler] {
    const {cId} = useParams()
    const service = useContext(ChannelServiceContext)
    const [state, dispatcher] = useReducer(
        reduce,
        makeChannelInitialState()
    )
    // const [notification_msg, remove_msg] = useContext(SseMessageContext)
    const handler: UseChannelHandler = {
        initChannel(): void {
            if (state.tag !== "idle") return
            service
                .loadChannel(cId)
                .then((response) => {
                    if (response.tag === "success") dispatcher({type: "loaded-channel", channel: response.value})
                    else dispatcher({type: "error", error: response.value, previous: state})
                })
            dispatcher({type: "startUp"})
        },
        loadMore(): void {
            if (state.tag !== "loaded") return
            const messages = state.messages
            messages.map((message) => {
                message.timestamp
            })
            messages.sort()
            service
                .loadMore(cId, messages[messages.length - 1].timestamp.toString(), LIMIT + 1)
                .then((response) => {
                    if (response.tag === "success") {
                        dispatcher({
                            type: "loaded-more",
                            messages: response.value,
                            hasMore: response.value.length > LIMIT
                        })
                    } else {
                        dispatcher({type: "error", error: response.value, previous: state})
                    }
                })
        },
        sendMsg(msg: string): void {
            service
                .sendMsg(cId, msg)
                .then((response) => {
                    if (response.tag === "success") {
                        dispatcher({type: "mag-sent"})
                    } else {
                        dispatcher({type: "error", error: response.value, previous: state})
                    }
                })
        }
    }
    return [state, handler]
}