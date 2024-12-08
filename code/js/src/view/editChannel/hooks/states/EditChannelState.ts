import {Channel} from "../../../../model/Channel";

/**
 * State for the EditChannelView
 */
export type EditChannelState =
    | { tag: "idle" }
    | { tag: "loading" }
    | { tag: "editing" , channel: Channel }
    | { tag: "error", message: string }
    | { tag: "submitting" }
    | { tag: "redirect", cId: string }