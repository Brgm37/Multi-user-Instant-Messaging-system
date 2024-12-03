import {UseScrollHandler, UseScrollState} from "../../../../../service/utils/hooks/useScroll/UseScroll";
import {compareTimestamps, Message} from "../../../../../model/Message";

/**
 * Add a message list to the handler. Removing
 * the previous list and adding the new list.
 *
 * @param handler
 * @param messages
 * @param limit
 * @param hasMoreFlag
 */
export function initList(
    handler: UseScrollHandler<Message>,
    messages: Message[],
    limit: number,
    hasMoreFlag: number
): void {
    const list = messages.slice(0, limit)
    const hasMore = {head: false, tail: messages.length === hasMoreFlag}
    handler.reset(list, hasMore)
}

/**
 * Add a message to the handler.
 *
 * @param handler
 * @param list
 * @param message
 */
export function addMessage(
    handler: UseScrollHandler<Message>,
    list: UseScrollState<Message>,
    message: Message
): void {
    const messages = [message]
    if (list.hasMore.head) return
    else handler.addItems(messages, "head", list.hasMore)
}


/**
 * Add items to the handler.
 *
 * @param at
 * @param listHandler
 * @param list
 * @param result
 * @param limit
 * @param defaultLimit
 * @param hasMoreFlag
 * @param listSize
 */
export function addItems(
    at: "head" | "tail",
    listHandler: UseScrollHandler<Message>,
    list: UseScrollState<Message>,
    result: Message[],
    limit: number,
    defaultLimit: number,
    hasMoreFlag: number,
    listSize: number
): void {
    const messages = result.slice(0, limit)
    if (messages.length < limit) limit = messages.length
    else limit = defaultLimit
    const tail =
        result.length === hasMoreFlag && at === "tail" ||
        at === "head" && list.list.length === listSize
    const hasMore = {
        head: compareTimestamps(messages[messages.length - 1], list.list[0]) < 0 && list.list.length === listSize,
        tail
    }
    listHandler.addItems(messages, at, hasMore)
}