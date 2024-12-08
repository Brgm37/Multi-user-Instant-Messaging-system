/**
 * @description Type for the state of the PublicChannels form.
 *
 * @type FindChannelState
 */
export type FindChannelState =
    | { tag: "idle" }
    | { tag: "loading"; at: "head" | "tail" }
    | { tag: "scrolling"; }
    | { tag: "redirect"; channelId: number }
    | { tag: "error"; error: string; }
    | { tag: "joining"; channelId: number }
