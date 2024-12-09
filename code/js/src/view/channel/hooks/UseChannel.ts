import {ChannelState} from "./states/ChannelState";
import {useContext, useEffect, useReducer, useRef} from "react";
import {useParams} from "react-router-dom";
import {ChannelServiceContext} from "../../../service/channel/ChannelServiceContext";
import useScroll, {UseScrollHandler, UseScrollState} from "../../../service/utils/hooks/useScroll/UseScroll";
import {compareTimestamps, Message} from "../../../model/Message";
import envConfig from "../../../../envConfig.json";
import reduce from "./reducer/Reducer";
import {SseCommunicationServiceContext} from "../../../service/sse/SseCommunicationService";
import {UseChannelHandler} from "./handler/UseChannelHandler";
import {AuthUserContext} from "../../session/AuthUserContext";

/**
 * The default values.
 */
const LIST_SIZE = envConfig.messages_limit
const DEFAULT_LIMIT = envConfig.default_messages_limit
let limit = DEFAULT_LIMIT
const HAS_MORE = DEFAULT_LIMIT + 1


/**
 * Add a message list to the handler. Removing
 * the previous list and adding the new list.
 *
 * @param handler
 * @param messages
 */
function initList(
    handler: UseScrollHandler<Message>,
    messages: Message[],
): void {
    const list = messages.slice(0, limit)
    if (list.length < limit) limit = list.length
    else limit = DEFAULT_LIMIT
    const hasMore = {head: false, tail: messages.length === HAS_MORE}
    handler.reset(list, hasMore)
}

/**
 * Add a message to the handler.
 *
 * @param handler
 * @param list
 * @param message
 */
function addMessage(
    handler: UseScrollHandler<Message>,
    list: UseScrollState<Message>,
    message: Message
): void {
    const messages = [message]
    if (list.hasMore.head) handler.reset(list.list, list.hasMore)
    else handler.addItems(messages, "head", list.hasMore)
}

/**
 * Add items to the handler.
 *
 * @param at
 * @param listHandler
 * @param list
 * @param result
 */
function addItems(
    at: "head" | "tail",
    listHandler: UseScrollHandler<Message>,
    list: UseScrollState<Message>,
    result: Message[],
): void {
    const messages = result.slice(0, limit)
    if (messages.length < limit) limit = messages.length
    else limit = DEFAULT_LIMIT
    const tail =
        result.length === HAS_MORE && at === "tail" ||
        at === "head" && list.list.length === list.max
    const hasMore = {
        head: compareTimestamps(messages[messages.length - 1], list.list[0]) < 0 && list.list.length === list.max,
        tail
    }
    listHandler.addItems(messages, at, hasMore)
}

/**
 * The hook to use the channel reducer.
 */
export function useChannel(): [ChannelState, UseScrollState<Message>, UseChannelHandler] {
    const {messages, consumeMessage} = useContext(SseCommunicationServiceContext)
    const service = useContext(ChannelServiceContext)
    const {id} = useParams()
    const authContext = useContext(AuthUserContext)
    const [list, listHandler] = useScroll<Message>(LIST_SIZE)
    const [state, dispatch] = useReducer(reduce, {tag: "idle"})
    const isInitialMount = useRef(true);

    useEffect(() => {
        if (isInitialMount.current) isInitialMount.current = false;
        else {
            if (state.tag === "idle") return
            limit = DEFAULT_LIMIT
            dispatch({tag: "reset"})
        }
    }, [id]);

    useEffect(() => {
        if (state.tag !== "loading") return
        if (state.at === "sending") dispatch({tag: "sendSuccess"})
        else dispatch({tag: "loadSuccess"})
    }, [list]);

    useEffect(() => {
        if (messages.length === 0) return
        if (state.tag === "idle" || state.tag === "loading") return
        const consumed: Message[] = []
        let first: boolean = true
        messages.forEach(msg => {
            if (msg.channel == id) {
                consumed.push(msg)
                if (!list.list.some(it => it.id === msg.id) && Number(authContext.id) !== Number(msg.owner.id)) {
                    if (first) {
                        first = false
                        dispatch({tag: "receiving-sse"})
                    }
                    addMessage(listHandler, list, msg)
                }
            }
        })
        consumeMessage(consumed)
    }, [messages, state]);

    const handler: UseChannelHandler = {
        error(error: string): void {
            dispatch({tag: "error", error})
        },
        reset() {
            if (state.tag !== "messages") return
            dispatch({tag: "reload"})
        },
        initChannel(): void {
            if (state.tag !== "idle") return
            service
                .loadMore(id, "0", HAS_MORE, "before")
                .then(response => {
                    if (response.tag === "success") initList(listHandler, response.value)
                    else dispatch({tag: "loadError", error: response.value, previous: state})
                })
            dispatch({tag: "init"})
        },
        loadMore(at: "head" | "tail"): void {
            if (state.tag !== "messages") return
            if (at === "head" && !list.hasMore.head || at === "tail" && !list.hasMore.tail) return
            const timestamp = at === "head" ? list.list[0].timestamp : list.list[list.list.length - 1].timestamp
            const befAft = at === "head" ? "after" : "before"
            service
                .loadMore(id, timestamp, HAS_MORE, befAft)
                .then(response => {
                    if (response.tag === "success") addItems(at, listHandler, list, response.value)
                    else dispatch({tag: "loadError", error: response.value, previous: state})
                })
            dispatch({tag: "loadMore", at})
        },
        sendMsg(msg: string): void {
            if (state.tag === "idle" || state.tag === "error") return
            service
                .sendMsg(id, msg)
                .then(response => {
                    if (response.tag === "success") addMessage(listHandler, list, response.value)
                    else dispatch({tag: "sendError", error: response.value, previous: state})
                })
            dispatch({tag: "sendMessage"})
        },
        goBack() {
            if (state.tag !== "error") return
            dispatch({tag: "go-back"})
        }
    }

    return [state, list, handler]
}