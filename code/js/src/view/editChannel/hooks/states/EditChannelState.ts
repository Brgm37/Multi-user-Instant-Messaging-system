import {Channel} from "../../../../model/Channel";

/**
 * State for the EditChannelView
 *
 * The states are:
 * - idle: The channel is not being edited.
 * - loading: The channel is being loaded.
 * - editing: The channel is being edited.
 * - error: The channel has an error.
 * - submitting: The channel is being submitted.
 * - redirect: Redirecting.
 *
 * @type EditChannelState
 * @param tag - The state of the channel.
 * @param channel - The channel.
 * @param message - The message of the channel.
 * @param cId - The channel id.
 */
export type EditChannelState =
    | { tag: "idle" }
    | { tag: "loading" }
    | { tag: "editing" , channel: Channel }
    | { tag: "error", message: string }
    | { tag: "submitting" }
    | { tag: "redirect", cId: string }