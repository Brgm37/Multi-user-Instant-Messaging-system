/**
 * The state of the find channels component.
 *
 * The state can be one of the following:
 * - idle: The component is idle.
 * - loading: The component is loading.
 * - scrolling: The component is scrolling.
 * - redirect: The component is redirecting.
 * - error: The component has an error.
 * - joining: The component is joining.
 *
 * @type FindChannelState
 * @property tag - The tag of the state.
 * @property at - The position of the channel.
 * @property channelId - The channel id.
 * @property error - The error message.
 */
export type FindChannelState =
    | { tag: "idle" }
    | { tag: "loading"; at: "head" | "tail" }
    | { tag: "scrolling"; }
    | { tag: "redirect"; channelId: number }
    | { tag: "error"; error: string; }
    | { tag: "joining"; channelId: number }
