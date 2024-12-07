import {ChannelInput} from "../states/createChannelsState";

export type UseCreateChannelHandler = {
    /**
     * The function to call when the name changes.
     * @param name The new name.
     * @returns void
     */
    onNameChange: (name: string) => void

    /**
     * The function to call when the description changes.
     * @param description The new description.
     * @returns void
     */
    onDescriptionChange: (description: string) => void
    /**
     * The function to call to go back to the editing state.
     * @returns void
     */
    goBack(): void
    /**
     * The function to call when the user clicks on the submit button.
     *
     * @returns void
     */
    onSubmit: (channel: ChannelInput) => void
}