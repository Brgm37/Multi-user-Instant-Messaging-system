import {Channel} from "../../../../model/Channel";

/**
 * The edit channel action.
 *
 * The action types are:
 * - init: The channel is being initialized.
 * - loadSuccess: The channel was successfully loaded.
 * - loadError: An error occurred while loading the channel.
 * - editSuccess: The channel was successfully edited.
 * - editError: An error occurred while editing the channel.
 * - submit: The form is being submitted.
 * - redirect: Redirect to the previous state.
 * - closeError: Close the error.
 *
 * @type EditChannelAction
 * @property type - The type of action.
 * @property channel - The channel.
 * @property error - The error message.
 * @property cId - The channel id.
 */
export type EditChannelAction =
    | { type: "init" }
    | { type: "loadSuccess", channel: Channel }
    | { type: "loadError", error: string }
    | { type: "editSuccess", cId: string }
    | { type: "editError", error: string }
    | { type: "submit" }
    | { type: "redirect" }
    | { type: "closeError" }