import {ChannelInput} from "../states/createChannelsState";

/**
 * The handler for the create channel view.
 *
 * The handler is used to interact with the create channel view.
 * It provides methods to handle the name and description changes,
 * and to submit the channel.
 *
 * @method onNameChange - The function to call when the name changes.
 * @method onDescriptionChange - The function to call when the description changes.
 * @method onSubmit - The function to call when the user clicks on the submit button.
 */
export type UseCreateChannelHandler = {
    onNameChange: (name: string) => void
    onDescriptionChange: (description: string) => void
    onSubmit: (channel: ChannelInput) => void
}