import {AccessControl} from "../../../../model/AccessControl";

/**
 * The visibility.
 */
export type Visibility = "public" | "private"

/**
 * The channel input.
 */
export type ChannelInput = {
    name: string,
    visibility: Visibility,
    access: AccessControl,
    isValid?: boolean,
    description?: string,
    icon?: string
}

/**
 * The create channels state.
 *
 * The states are:
 * - editing: The channel is being edited.
 * - error: The channel has an error.
 * - submitting: The channel is being submitted.
 * - redirecting: The channel is being redirected.
 * - validating: The channel is being validated.
 *
 * @type CreateChannelsState
 * @param tag - The state of the channel.
 * @param input - The input of the channel.
 * @param message - The message of the channel.
 */
export type CreateChannelsState =
    { tag: "editing", input: ChannelInput, message?: string} |
    { tag: "error", message: string, input: ChannelInput} |
    { tag: "submitting", input: ChannelInput } |
    { tag: "redirecting", input: ChannelInput } |
    { tag: "validating", input: ChannelInput }

/**
 * Make the initial state.
 */
export function makeInitialState(): CreateChannelsState {
    return {
        tag: "editing",
        input: {
            name: "",
            visibility: "public",
            access: "READ_ONLY",
        },
    }
}