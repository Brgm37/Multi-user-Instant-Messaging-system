import {ChannelInput} from "./createChannelsState";

/**
 * Action types for the createChannelsState
 *
 * The action types are:
 * - editName: The name is being edited.
 * - editDescription: The description is being edited.
 * - error: An error occurred.
 * - success: The channel was successfully created.
 * - submit: The form is being submitted.
 * - go-back: Go back to the previous state.
 * - validation: The input is being validated.
 * - validated: The input has been validated.
 *
 * @type CreateChannelsAction
 * @property type - The type of action.
 * @property inputValue - The input value.
 * @property message - The message.
 * @property input - The channel input.
 * @property name - The name.
 * @property isValidInput - The validation status.
 */
export type CreateChannelsAction =
    { type: "editName", inputValue: string} |
    { type: "editDescription", inputValue: string} |
    { type: "error", message: string} |
    { type: "success", input: ChannelInput } |
    { type: "submit" } |
    { type: "go-back" } |
    { type: "validation", name: string} |
    { type: "validated", isValidInput: boolean }
